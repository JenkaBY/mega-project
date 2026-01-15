package com.github.jenkaby.config.messaging;

import lombok.AllArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@AllArgsConstructor
@Configuration
public class TestKafkaMessageConsumerConfig {

    private final KafkaProperties kafkaProperties;
    private final ObjectProvider<SslBundles> sslBundles;
    @Value("${spring.test.kafka.consumer.group-id}")
    private final String testConsumerGroupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> testStringKafkaContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        var consumerProps = kafkaProperties.buildConsumerProperties(sslBundles.getIfAvailable());
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerProps));
        factory.getContainerProperties().setGroupId(testConsumerGroupId);
        factory.setAutoStartup(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> testJsonKafkaContainerFactory(
            @Qualifier("jsonConsumerFactory") ConsumerFactory<String, Object> jsonConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();

        factory.setConsumerFactory(jsonConsumerFactory);
        factory.getContainerProperties().setGroupId(testConsumerGroupId);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ConcurrentKafkaListenerContainerFactory<String, Object> testAvroContainerFactory(
            @Qualifier("avroConsumerFactory") ConsumerFactory<String, ? super SpecificRecord> avroConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory((ConsumerFactory<String, Object>) avroConsumerFactory);
        factory.getContainerProperties().setGroupId(testConsumerGroupId);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }
}
