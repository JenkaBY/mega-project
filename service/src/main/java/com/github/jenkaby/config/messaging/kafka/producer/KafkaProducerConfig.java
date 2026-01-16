package com.github.jenkaby.config.messaging.kafka.producer;


import com.github.jenkaby.messaging.base.JsonPayload;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;
    private final ObjectProvider<SslBundles> sslBundles;

    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactory() {
        var delegatingByTypeSerializer = new DelegatingByTypeSerializer(
                new LinkedHashMap<>(
                        Map.ofEntries(
                                Map.entry(byte[].class, new ByteArraySerializer()),
                                Map.entry(Byte.class, new BytesSerializer()),
                                Map.entry(String.class, new StringSerializer()),
                                Map.entry(SpecificRecord.class, new KafkaAvroSerializer()),
                                Map.entry(GenericRecord.class, new KafkaAvroSerializer()),
                                Map.entry(JsonPayload.class, new JsonSerializer<>())
                        )
                ), true // required to be able to convert to parent class
        );

        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildConsumerProperties(sslBundles.getIfAvailable()),
                new StringSerializer(), // key serializer
                delegatingByTypeSerializer // value serializer
        );
    }

    @Bean
    @SuppressWarnings({"unchecked", "raw"}) // to avoid ConsumerFactory raw type warning
    public KafkaTemplate<?, ?> avroKafkaTemplate(@Qualifier("avroConsumerFactory") ConsumerFactory avroConsumerFactory) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory());
        log.info("Initialized avro kafkaTemplate {}", kafkaTemplate);
        kafkaTemplate.setConsumerFactory(avroConsumerFactory);
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, Object> commonKafkaTemplate() {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory());
        log.info("Initialized common kafkaTemplate {}", kafkaTemplate);
        return kafkaTemplate;
    }
}
