package com.github.jenkaby.service.delay;

import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.DelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AopExecutionAroundClientDelayService implements ClientDelayService {

    private final DelayService delayService;

    @Override
    public void delegateInvocation(long delayMs) {
        log.debug("[AOP execution] Start delay at {}", LocalDateTime.now());
        delayService.makeDelay(delayMs);
        log.debug("[AOP execution] End delay at {}", LocalDateTime.now());
    }

    @Override
    public String getImplementationType() {
        return ClientDelayService.constructKey(TelemetryTag.TYPE_AOP_EXECUTION);
    }
}
