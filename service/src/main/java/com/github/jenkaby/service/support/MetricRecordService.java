package com.github.jenkaby.service.support;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service
public class MetricRecordService {

    private final MeterRegistry registry;

    public void recordLatency(String metric, Tag[] tags, Duration duration) {
        log.debug("Recording latency for {} metric", metric);
        registry.timer(metric, Arrays.asList(tags)).record(duration);
    }
}
