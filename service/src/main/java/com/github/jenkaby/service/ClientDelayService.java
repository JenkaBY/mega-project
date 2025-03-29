package com.github.jenkaby.service;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.support.annotation.TrackLatency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientDelayService {

    private final DelayService delayService;

    @TrackLatency(
            metric = TelemetryCounter.DELAY_SERVICE_LATENCY,
            tags = TelemetryTag.TYPE_AOP_ANNOTATION)
    public void invokeMakeDelay(long delayMs) {
        log.debug("Start delay at {}", LocalDateTime.now());
        delayService.makeDelay(delayMs);
        log.debug("End delay at {}", LocalDateTime.now());
    }

    public void aopWrappedInvokeMakeDelay(long delayMs) {
        log.debug("[aop wrapped] Start delay at {}", LocalDateTime.now());
        delayService.makeDelay(delayMs);
        log.debug("[aop wrapped] End delay at {}", LocalDateTime.now());
    }
}
