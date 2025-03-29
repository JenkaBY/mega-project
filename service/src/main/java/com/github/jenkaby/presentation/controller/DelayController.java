package com.github.jenkaby.presentation.controller;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.ClientDelayService;
import com.github.jenkaby.service.DelayService;
import com.github.jenkaby.service.support.MeasurementService;
import com.github.jenkaby.service.support.MetricRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RequestMapping("/api/delay")
@RequiredArgsConstructor
@RestController
public class DelayController {

    private final ClientDelayService clientDelayService;
    private final DelayService delayService;
    private final MeasurementService measurementService;
    private final MetricRecordService metricRecordService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/aop-annotation")
    public String invokeAopAnnotationLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /aop-annotation");
        clientDelayService.invokeMakeDelay(delay);
        return response(delay);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/aop-execution")
    public String invokeAopExecutionLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /aop-execution");
        clientDelayService.aopWrappedInvokeMakeDelay(delay);
        return response(delay);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/native")
    public String invokeNativeLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /native");
        var measured = measurementService.measure(() -> {
            delayService.makeDelay(delay);
            return null;
        });
        var metric = TelemetryCounter.DELAY_SERVICE_LATENCY.getMetricName();
        var tags = TelemetryTag.TYPE_NATIVE.getTags();
        var latency = Duration.ofNanos(measured.getNanos());
        metricRecordService.recordLatency(metric, tags, latency);
        return response(delay);
    }

    private String response(long delay) {
        return "OK after " + delay + " ms delay";
    }
}
