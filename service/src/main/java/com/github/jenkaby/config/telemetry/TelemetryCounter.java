package com.github.jenkaby.config.telemetry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TelemetryCounter {

    DELAY_SERVICE_LATENCY("delay.service.latency", "Measured latency of the DelayService invocation"),
    ;

    private final String metricName;
    private final String description;
}
