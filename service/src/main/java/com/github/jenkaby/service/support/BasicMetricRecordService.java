package com.github.jenkaby.service.support;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class BasicMetricRecordService implements MetricRecordService {

    private final MeterRegistry registry;

    @Override
    public void recordLatency(String metric, Tag[] tags, Duration duration) {
        log.debug("Recording latency {} for {} metric", duration, metric);
        registry.timer(metric, Arrays.asList(tags)).record(duration);
    }
}
