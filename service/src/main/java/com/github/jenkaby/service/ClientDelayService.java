package com.github.jenkaby.service;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.support.annotation.RecordMetric;
import com.github.jenkaby.service.support.annotation.TrackLatency;

public interface ClientDelayService {

    @TrackLatency(
            metric = TelemetryCounter.DELAY_SERVICE_LATENCY,
            tags = TelemetryTag.TYPE_AOP_ANNOTATION)
    void invokeMakeDelay(long delayMs);

    @RecordMetric(
            metric = TelemetryCounter.DELAY_SERVICE_LATENCY,
            tags = TelemetryTag.TYPE_BPP)
    void bbpInvokeMakeDelay(long delayMs);

    void aopWrappedInvokeMakeDelay(long delayMs);

    void timedMicrometerInvokeMakeDelay(long delayMs);
}
