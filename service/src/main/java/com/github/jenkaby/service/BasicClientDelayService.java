package com.github.jenkaby.service;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.support.annotation.RecordMetric;
import com.github.jenkaby.service.support.annotation.TrackLatency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicClientDelayService implements ClientDelayService {

    private final DelayService delayService;

    @TrackLatency(
            metric = TelemetryCounter.DELAY_SERVICE_LATENCY,
            tags = TelemetryTag.TYPE_AOP_ANNOTATION)
    @Override
    public void invokeMakeDelay(long delayMs) {
        log.debug("[AOP annotation] Start delay at {}", LocalDateTime.now());
        delayService.makeDelay(delayMs);
        log.debug("[AOP annotation] End delay at {}", LocalDateTime.now());
    }

    @RecordMetric(
            metric = TelemetryCounter.DELAY_SERVICE_LATENCY,
            tags = TelemetryTag.TYPE_BPP)
    @Override
    public void bbpInvokeMakeDelay(long delayMs) {
        log.debug("[BPP] Start delay at {}", LocalDateTime.now());
        delayService.makeDelay(delayMs);
        log.debug("[BPP] End delay at {}", LocalDateTime.now());
    }

    @Override
    public void aopWrappedInvokeMakeDelay(long delayMs) {
        log.debug("[aop wrapped] Start delay at {}", LocalDateTime.now());
        delayService.makeDelay(delayMs);
        log.debug("[aop wrapped] End delay at {}", LocalDateTime.now());
    }
}
