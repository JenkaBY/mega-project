package com.github.jenkaby.service.delay;

import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.DelayService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimedMicrometerClientDelayService implements ClientDelayService {

    private final DelayService delayService;

    @Timed(value = "delay.service.latency", extraTags = {"type", "timed"})
    @Override
    public void delegateInvocation(long delayMs) {
        log.debug("[@Timed micrometer] Start delay at {}", LocalDateTime.now());
        delayService.makeDelay(delayMs);
        log.debug("[@Timed micrometer] End delay at {}", LocalDateTime.now());
    }

    @Override
    public String getImplementationType() {
        return ClientDelayService.constructKey(TelemetryTag.TYPE_TIMED);
    }
}
