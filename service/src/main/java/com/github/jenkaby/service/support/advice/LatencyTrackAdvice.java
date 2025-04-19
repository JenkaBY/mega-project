package com.github.jenkaby.service.support.advice;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.support.MeasurementService;
import com.github.jenkaby.service.support.MetricRecordService;
import com.github.jenkaby.service.support.annotation.TrackLatency;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LatencyTrackAdvice {

    private final MeasurementService measurementService;
    private final MetricRecordService metricRecordService;

    @Around("@annotation(com.github.jenkaby.service.support.annotation.TrackLatency)")
    public Object measureLatency(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature sig = joinPoint.getSignature();
        log.debug("[AOP Annotation] Track latency for {}", sig.toShortString());
        var measured = measurementService.measure(joinPoint::proceed);
        log.info("[AOP Annotation] Measured latency for {} is {} ns", sig.toShortString(), measured.getNanos());

        // Method Information and send metric
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        TrackLatency latencyAnnotation = method.getAnnotation(TrackLatency.class);
        String metricName = latencyAnnotation.metric().getMetricName();
        Tag[] tags = Arrays.stream(latencyAnnotation.tags())
                .map(TelemetryTag::getTags)
                .flatMap(Arrays::stream)
                .toArray(Tag[]::new);
        metricRecordService.recordLatency(metricName, tags, Duration.of(measured.getNanos(), ChronoUnit.NANOS));

        return measured.value();
    }

    @Around("execution(* com.github.jenkaby.service.delay.AopExecutionAroundClientDelayService.delegateInvocation(..))")
    public Object measureLatencyForAopWrapped(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature sig = joinPoint.getSignature();
        log.debug("[AOP Execution] Track latency for {}", sig.toShortString());
        var measured = measurementService.measure(joinPoint::proceed);
        log.info("[AOP Execution] Measured latency for {} is {} ns", sig.toShortString(), measured.getNanos());

        // send metric
        String metricName = TelemetryCounter.DELAY_SERVICE_LATENCY.getMetricName();
        Tag[] tags = TelemetryTag.TYPE_AOP_EXECUTION.getTags();
        metricRecordService.recordLatency(metricName, tags, Duration.of(measured.getNanos(), ChronoUnit.NANOS));

        return measured.value();
    }
}
