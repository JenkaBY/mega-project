package com.github.jenkaby.config.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SkipHeaderRecordFilterStrategy<T> implements RecordFilterStrategy<String, T> {

    private static final String SHOULD_SKIP_HEADER_VALUE = Boolean.TRUE.toString();
    private static final String SHOULD_SKIP_HEADER = "should-skip";

    @Override
    public boolean filter(ConsumerRecord<String, T> record) {
        var discard = false;
        var headers = record.headers();
        var shouldSkipHeader = headers.lastHeader(SHOULD_SKIP_HEADER);

        if (shouldSkipHeader != null) {
            discard = Optional.ofNullable(shouldSkipHeader.value())
                    .map(val -> new String(val, StandardCharsets.UTF_8))
                    .map(SHOULD_SKIP_HEADER_VALUE::equalsIgnoreCase)
                    .orElse(false);
        }

        if (discard) {
            log.info("Message with key {} discarded for topic {}", record.key(), record.topic());
        }
        return discard;
    }
}
