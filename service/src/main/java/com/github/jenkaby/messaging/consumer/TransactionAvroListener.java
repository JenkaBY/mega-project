package com.github.jenkaby.messaging.consumer;

import com.github.jenkaby.megaapp.avro.payload.v0.TransactionEventAvro;
import com.github.jenkaby.model.TransactionSourceMetadata;
import com.github.jenkaby.presentation.controller.mapper.TransactionEventMapper;
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
public class TransactionAvroListener {

    private final MessageLogService logService;
    private final TransactionEventMapper mapper;

    @KafkaListener(topics = {
            "${app.kafka.topics.transaction.name}"
    }, containerFactory = "avroListenerContainerFactory")
    public void onTransactionEventHandler(@Headers Map<String, Object> headers, @Payload TransactionEventAvro payload,
                                          @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
                                          ConsumerRecord<String, TransactionEventAvro> raw,
                                          Acknowledgment acknowledgment) {
        log.info("MessageKey [{}] Payload [{}]", messageKey, payload);
        headers.forEach((headerKey, value) -> log.debug("Header: {}={}", headerKey, value));
        if (messageKey != null && messageKey.toLowerCase().contains("error")) {
            throw new RuntimeException("Exception based on message key");
        }

        var event = mapper.toEntity(payload);
        logService.save(event, TransactionSourceMetadata.builder()
                .source("%s:%s:%s".formatted(raw.topic(), raw.partition(), raw.offset()))
                .encoding("AVRO")
                .headers(headers)
                .modified(this.getClass().getCanonicalName())
                .build());
        acknowledgment.acknowledge();
    }
}
