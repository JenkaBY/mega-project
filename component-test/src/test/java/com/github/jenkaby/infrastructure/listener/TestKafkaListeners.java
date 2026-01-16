package com.github.jenkaby.infrastructure.listener;

import com.github.jenkaby.context.LocalMessagesStore;
import com.github.jenkaby.megaapp.avro.payload.v0.TransactionEventAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestKafkaListeners {

    private final LocalMessagesStore messagesStore;

    @KafkaListener(
            topics = {"${app.kafka.topics.message.name}", "${app.kafka.topics.message.dlt}"},
            containerFactory = "testStringKafkaContainerFactory"
    )
    public void handleNotificationTopic(
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
            ConsumerRecord<String, String> message) {
        log.info("[TEST] Message received '{}' from topic '{}'", message.value(), topic);
        messagesStore.receiveMsg(topic, message);
    }

    @KafkaListener(
            topics = {"${app.kafka.topics.transaction-json.name}", "${app.kafka.topics.transaction-json.dlt}"},
            containerFactory = "testJsonKafkaContainerFactory"
    )
    public void handleTransactionJsonTopic(
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
            ConsumerRecord<String, ?> message) {
        log.info("[TEST] JSON Message received '{}' from topic '{}'", message.value().getClass(), topic);
        log.debug("[TEST] JSON Message header size '{}' from topic '{}'", message.headers().toArray().length, topic);
        message.headers().forEach(
                h -> log.info("[TEST] JSON header key:'{}'='{}' from topic '{}'",
                        h.key(), new String(h.value(), StandardCharsets.UTF_8).intern(), topic)
        );
        messagesStore.receiveMsg(topic, message);
    }


    @KafkaListener(
            topics = {"${app.kafka.topics.transaction.name}", "${app.kafka.topics.transaction.dlt}"},
            containerFactory = "testAvroContainerFactory"
    )
    public void handleTransactionAvroTopic(
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
            ConsumerRecord<String, TransactionEventAvro> message) {
        log.info("[TEST] Message received '{}' from topic '{}'", message.value(), topic);
        messagesStore.receiveMsg(topic, message);
    }
}
