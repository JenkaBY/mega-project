package com.github.jenkaby.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        sendMessageToTopic(topic, Map.of(), kafkaMessageKey, payload);
    }

    public <T> void sendMessageToTopic(String topic, Map<String, String> headers, String msgKey, T payload) {
        log.info("Sending message for key={}", msgKey);
        var kafkaHeaders = new RecordHeaders(headers.entrySet().stream()
                .map(entry -> new RecordHeader(entry.getKey(), entry.getValue().getBytes(StandardCharsets.UTF_8)))
                .collect(Collectors.toSet()));
        ProducerRecord<Object, Object> toSend = new ProducerRecord<>(topic, null, null, msgKey, payload, kafkaHeaders);
        CompletableFuture<SendResult<Object, Object>> send = commonKafkaTemplate.send(toSend);
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
