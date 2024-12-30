package com.github.jenkaby.infrastructure.listener;

import com.github.jenkaby.context.LocalMessagesStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestKafkaListeners {

    private final LocalMessagesStore messagesStore;

    @KafkaListener(
            topics = {"${app.kafka.topics.message.name}", "${app.kafka.topics.message.dlt}"},
//          groupId is set on containerFactory level to be able to reused it in other container factories
//            groupId = "${spring.test.kafka.consumer.group-id}",
            containerFactory = "testStringKafkaContainerFactory"
    )
    public void handleNotificationTopic(
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
            ConsumerRecord<String, String> message) {
        log.info("[TEST] Message received '{}' from topic '{}'", message.value(), topic);
        messagesStore.receiveMsg(topic, message);
    }
}
