package com.github.jenkaby.messaging.consumer;

import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.model.TransactionSourceMetadata;
import com.github.jenkaby.service.MessageLogService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Component
public class TransactionJsonListener {

    private final MessageLogService logService;

    @KafkaListener(topics = {
            "${app.kafka.topics.transaction-json.name}"
    }, containerFactory = "jsonListenerContainerFactory")
    public void onTransactionEventHandler(@Headers Map<String, Object> headers, @Payload TransactionEvent payload,
                                          @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
                                          ConsumerRecord<String, TransactionEvent> raw,
                                          Acknowledgment acknowledgment) {
        log.info("JSON MessageKey [{}] Payload [{}]", messageKey, payload);
        if (messageKey != null && messageKey.toLowerCase().contains("error")) {
            throw new RuntimeException("Exception based on message key");
        }

        logService.save(payload, TransactionSourceMetadata.builder()
                .source("%s:%s:%s".formatted(raw.topic(), raw.partition(), raw.offset()))
                .encoding("JSON")
                .headers(headers)
                .modified(this.getClass().getCanonicalName())
                .build());
        acknowledgment.acknowledge();
    }
}
