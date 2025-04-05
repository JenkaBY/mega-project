package com.github.jenkaby.service.delay;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.DelayService;
import com.github.jenkaby.service.support.ProxyType;
import com.github.jenkaby.service.support.annotation.RecordMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BppDynamicProxyClientDelayService implements ClientDelayService {

    private final DelayService delayService;

    @RecordMetric(
            metric = TelemetryCounter.DELAY_SERVICE_LATENCY,
            tags = TelemetryTag.TYPE_BPP_DYNAMIC_PROXY,
            proxyType = ProxyType.JDK_DYNAMIC)
    @Override
    public void delegateInvocation(long delayMs) {
        log.debug("[BPP DynamicProxy] Start delay at {}", LocalDateTime.now());
        delayService.makeDelay(delayMs);
        log.debug("[BPP DynamicProxy] End delay at {}", LocalDateTime.now());
    }

    @Override
    public String getImplementationType() {
        return ClientDelayService.constructKey(TelemetryTag.TYPE_BPP_DYNAMIC_PROXY);
    }
}
