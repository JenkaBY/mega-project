package com.github.jenkaby.service.delay;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
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

    @Timed(value = TelemetryCounter.Constants.DELAY_SERVICE_LATENCY,
            extraTags = {TelemetryTag.Constants.TYPE, TelemetryTag.Constants.TIMED_MICROMETER})
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
