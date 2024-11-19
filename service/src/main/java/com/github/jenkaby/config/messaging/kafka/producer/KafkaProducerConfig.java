package com.github.jenkaby.config.messaging.kafka.producer;


import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;

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
                                Map.entry(GenericRecord.class, new KafkaAvroSerializer())
                        )
                ), true // required to be able to convert to parent class
        );

        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildConsumerProperties(sslBundles.getIfAvailable()),
                new StringSerializer(),
                delegatingByTypeSerializer
        );
    }

    @Bean
    public KafkaTemplate<?, ?> avroKafkaTemplate(ConsumerFactory avroConsumerFactory) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory());
        log.info("Initialized kafkaTemplate {}", kafkaTemplate);
        kafkaTemplate.setConsumerFactory(avroConsumerFactory);
        return kafkaTemplate;
    }


    @Bean
    public KafkaTemplate<String, Object> stringKafkaTemplate() {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory());
        log.info("Initialized string kafkaTemplate {}", kafkaTemplate);
        return kafkaTemplate;
    }
}
