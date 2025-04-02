package com.github.jenkaby.service.support;

import io.micrometer.core.instrument.Tag;

import java.time.Duration;

public interface MetricRecordService {
    void recordLatency(String metric, Tag[] tags, Duration duration);
}
