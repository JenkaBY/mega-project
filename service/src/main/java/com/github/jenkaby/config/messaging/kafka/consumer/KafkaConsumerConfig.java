package com.github.jenkaby.config.messaging.kafka.consumer;


import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;
    private final ObjectProvider<SslBundles> sslBundles;
    @Value("${app.kafka.dlt.retry.interval}")
    private final long backoffInterval;
    @Value("${app.kafka.dlt.retry.max-attempts}")
    private final int retryMaxAttempt;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaContainerFactory(
            KafkaTemplate<Object, Object> commonKafkaTemplate) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        var consumerProps = kafkaProperties.buildConsumerProperties(sslBundles.getIfAvailable());
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerProps));
        var commonErrorHandler = new DefaultErrorHandler(
                new SimpleDltRecoverer(commonKafkaTemplate),
                new FixedBackOff(backoffInterval, retryMaxAttempt));
        commonErrorHandler.setRetryListeners(new LoggingRetryListener());

//        don't acknowledge the message after handle
//        commonErrorHandler.setAckAfterHandle(false);
        factory.setCommonErrorHandler(commonErrorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> avroListenerContainerFactory(
            KafkaTemplate<Object, Object> commonKafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(avroConsumerFactory());

        var errorHandler = new DefaultErrorHandler(
                deadLetterRecoverer(commonKafkaTemplate),
                new FixedBackOff(backoffInterval, retryMaxAttempt));

        errorHandler.setRetryListeners(new LoggingRetryListener());
        factory.setCommonErrorHandler(errorHandler);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SpecificRecord> avroConsumerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties(sslBundles.getIfAvailable()));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//         avro properties
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
//        it should be picked up from app.yaml
//        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

//        ErrorHandlingDeserializer properties
//        VALUE_FUNCTION allows to pass failed record to a listener
//        props.put(ErrorHandlingDeserializer.VALUE_FUNCTION, FailedCatProvider.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);

        var consumerFactory = new DefaultKafkaConsumerFactory<String, SpecificRecord>(props);
        return consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> jsonListenerContainerFactory(
            KafkaTemplate<Object, Object> commonKafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jsonConsumerFactory());

//        allows to dynamic argument type matching for record listeners
        factory.setRecordMessageConverter(new JsonMessageConverter());

        var errorHandler = new DefaultErrorHandler(
                deadLetterRecoverer(commonKafkaTemplate),
                new FixedBackOff(backoffInterval, retryMaxAttempt));

        errorHandler.setRetryListeners(new LoggingRetryListener());
        factory.setCommonErrorHandler(errorHandler);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> jsonConsumerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties(sslBundles.getIfAvailable()));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        JsonDeserializer works with predefined type of payload but isn't universal solution
//        it's better for performance to use ByteArrayDeserializer instead of StringDeserializer
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ByteArrayDeserializer.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.github.jenkaby.model");

        var consumerFactory = new DefaultKafkaConsumerFactory<String, Object>(props);
        return consumerFactory;
    }

    public ConsumerRecordRecoverer deadLetterRecoverer(KafkaTemplate<Object, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                // destination resolver
                new BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition>() {
                    @Override
                    public TopicPartition apply(ConsumerRecord<?, ?> consumerRecord, Exception e) {
                        var dltName = consumerRecord.topic() + ".dlt";
                        log.info("Resolve topic : {}", dltName);
                        return new TopicPartition(dltName, 0);
                    }
                }
        );
    }

}