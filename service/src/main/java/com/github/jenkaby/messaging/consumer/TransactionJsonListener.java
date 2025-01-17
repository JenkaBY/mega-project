package com.github.jenkaby.messaging.consumer;

import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.service.TransactionJsonListenerService;
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

    private final TransactionJsonListenerService transactionJsonListenerService;

    @KafkaListener(topics = {
            "${app.kafka.topics.transaction-json.name}"
    }, containerFactory = "jsonListenerContainerFactory")
    public void onTransactionEventHandler(@Headers Map<String, Object> headers, @Payload TransactionEvent payload,
                                          @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
                                          ConsumerRecord<String, TransactionEvent> raw,
                                          Acknowledgment acknowledgment) {
        log.info("JSON MessageKey [{}] Payload [{}]", messageKey, payload);
        transactionJsonListenerService.process(headers, payload, messageKey, raw);
        acknowledgment.acknowledge();
    }
}
