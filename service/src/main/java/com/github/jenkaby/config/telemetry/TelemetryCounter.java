package com.github.jenkaby.config.telemetry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TelemetryCounter {

    DELAY_SERVICE_LATENCY(Constants.DELAY_SERVICE_LATENCY, "Measured latency of the DelayService invocation"),
    ;

    private final String metricName;
    private final String description;

    public static class Constants {
        public static final String DELAY_SERVICE_LATENCY = "delay.service.latency";
    }
}
