package com.github.jenkaby.service.delay;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.DelayService;
import com.github.jenkaby.service.support.MeasurementService;
import com.github.jenkaby.service.support.MetricRecordService;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NativeClientDelayService implements ClientDelayService {

    private static final String METRIC = TelemetryCounter.DELAY_SERVICE_LATENCY.getMetricName();
    private static final Tag[] TAGS = TelemetryTag.TYPE_NATIVE.getTags();

    private final DelayService delayService;
    private final MeasurementService measurementService;
    private final MetricRecordService metricRecordService;

    @Override
    public void delegateInvocation(long delayMs) {
        log.debug("[Native] Start delay at {}", LocalDateTime.now());
        var measured = measurementService.measure(() -> {
            delayService.makeDelay(delayMs);
            return null;
        });
        var latency = Duration.ofNanos(measured.getNanos());
        metricRecordService.recordLatency(METRIC, TAGS, latency);
        log.info("[Native] recorded latency {} ns", latency.toNanos());
        try {
            log.debug("Is there any measured value ? {}", measured.value() == null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getImplementationType() {
        return ClientDelayService.constructKey(TelemetryTag.TYPE_NATIVE);
    }
}
