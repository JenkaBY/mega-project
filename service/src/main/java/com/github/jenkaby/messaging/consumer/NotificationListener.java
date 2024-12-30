package com.github.jenkaby.messaging.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class NotificationListener {


    @KafkaListener(topics = {
            "${app.kafka.topics.message.name}"
    }, containerFactory = "stringKafkaContainerFactory")
    public void onNotificationMessageHandler(@Headers Map<String, Object> headers, @Payload String payload,
                                             @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
                                             ConsumerRecord<String, String> raw,
                                             Acknowledgment acknowledgment) {
        log.info("MessageKey [{}]", messageKey);
        log.info("Payload [{}]", payload);
        headers.forEach((headerKey, value) -> log.debug("Header: {}={}", headerKey, value));
        if (messageKey != null && messageKey.toLowerCase().contains("error")) {
            throw new RuntimeException("Exception based on message key");
        }

        acknowledgment.acknowledge();
    }
}
