package com.github.jenkaby.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
@Component
public class NotificationMessageSender {

    private final KafkaTemplate<Object, Object> commonKafkaTemplate;
    @Value("${app.kafka.topics.message.name}")
    private final String notificationTopicName;

    public void sendNotificationMessage(String kafkaMessageKey, String payload) {
        log.info("Sending message for key={}", kafkaMessageKey);
        CompletableFuture<SendResult<Object, Object>> send = commonKafkaTemplate.send(notificationTopicName, kafkaMessageKey, payload);
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
            log.info("Message[key={}] payload='{}' was delivered in the '{}[partition={}, offset={}]'", key, message, topic, partition, offset);
        });
    }


    public <T> void sendMessageToTopic(String topic, String kafkaMessageKey, T payload) {
        log.info("Sending message for key={}", kafkaMessageKey);
        CompletableFuture<SendResult<Object, Object>> send = commonKafkaTemplate.send(topic, kafkaMessageKey, payload);
        send.whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Exception occurred during sending message '{}' to the topic '{}'", payload, topic);
                return;
            }
            var message = res.getProducerRecord().value().toString();
            var key = res.getProducerRecord().key().toString();
            var topicActual = res.getRecordMetadata().topic();
            var partition = res.getRecordMetadata().partition();
            var offset = res.getRecordMetadata().offset();
            log.info("Message[key={}] payload='{}' was delivered on the '{}[partition={}, offset={}]'", key, message, topicActual, partition, offset);
        });
    }
}
