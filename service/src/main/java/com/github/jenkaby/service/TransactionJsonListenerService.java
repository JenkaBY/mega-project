package com.github.jenkaby.service;

import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.model.TransactionSourceMetadata;
import com.github.jenkaby.service.exception.KafkaNonRetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionJsonListenerService {

    private final MessageLogService logService;

    public void process(Map<String, Object> headers, TransactionEvent payload, String messageKey, ConsumerRecord<String, TransactionEvent> raw) {
        if (messageKey != null && messageKey.toLowerCase().contains("error")) {
            throw new RuntimeException("Exception based on message key");
        }
        if (messageKey != null && messageKey.equals("non-retryable-exception")) {
            throw new KafkaNonRetryableException("Message key trigger this exception. Kafka must not retry consuming in this case");
        }
        logService.save(payload, TransactionSourceMetadata.builder()
                .source("%s:%s:%s".formatted(raw.topic(), raw.partition(), raw.offset()))
                .encoding("JSON")
                .headers(headers)
                .modified(this.getClass().getCanonicalName())
                .build());
    }
}
