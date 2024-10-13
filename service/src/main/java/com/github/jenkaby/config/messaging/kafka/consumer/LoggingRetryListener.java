package com.github.jenkaby.config.messaging.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.lang.NonNull;

@Slf4j
public class LoggingRetryListener implements RetryListener {

    @Override
    public void failedDelivery(@NonNull ConsumerRecord<?, ?> record, Exception ex, int deliveryAttempt) {
        log.warn("Failed delivery {}. Delivery attempt {}. caused by {}",
                record, deliveryAttempt, ex.getCause().toString());

    }

    @Override
    public void recovered(@NonNull ConsumerRecord<?, ?> record, Exception ex) {
        log.warn("Recovered successfully {}. caused by {}",
                record, ex.getCause().toString());

    }
}