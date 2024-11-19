package com.github.jenkaby.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
@Component
public class NotificationMessageSender {

    @Value("${app.kafka.topics.message.name}")
    private String notificationTopicName;

    private final KafkaTemplate<Object, Object> stringKafkaTemplate;

    public void sendNotificationMessage(String kafkaMessageKey, String payload) {
        log.info("Sending message for key={}", kafkaMessageKey);
        CompletableFuture<SendResult<Object, Object>> send = stringKafkaTemplate.send(notificationTopicName, kafkaMessageKey, payload);
        send.whenComplete((res, ex) -> {
           if (ex != null) {
               log.error("Exception occurred during sending message '{}' to the topic '{}'", payload, notificationTopicName);
               return;
           }
           var message = res.getProducerRecord().value().toString();
           var key = res.getProducerRecord().key().toString();
           var topic = res.getRecordMetadata().topic();
           var partition = res.getRecordMetadata().partition();
           var offset = res.getRecordMetadata().offset();
           log.info("Message[key={}] payload='{}' was delivered in the '{}[part={}, offset={}]'", key, message, topic, partition, offset);
        });
    }
}
