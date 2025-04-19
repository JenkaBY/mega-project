package com.github.jenkaby.presentation.controller;

import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.DelayService;
import com.github.jenkaby.service.delay.ClientDelayService;
import com.github.jenkaby.service.delay.ClientDelayServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/delay")
@RequiredArgsConstructor
@RestController
public class DelayController {

    private final ClientDelayServiceContext clientDelayServiceContext;
    private final DelayService delayService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/aop-annotation")
    public String invokeAopAnnotationLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /aop-annotation");
        getClientByTag(TelemetryTag.TYPE_AOP_ANNOTATION).delegateInvocation(delay);
        return response(delay);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/aop-execution")
    public String invokeAopExecutionLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /aop-execution");
        getClientByTag(TelemetryTag.TYPE_AOP_EXECUTION).delegateInvocation(delay);
        return response(delay);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/bpp-dynamic")
    public String invokeBppDynamicExecutionLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /bpp-dynamic");
        getClientByTag(TelemetryTag.TYPE_BPP_DYNAMIC_PROXY).delegateInvocation(delay);
        return response(delay);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/bpp-cglib")
    public String invokeBppCgLibExecutionLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /bpp-cglib");
        getClientByTag(TelemetryTag.TYPE_BPP_CGLIB_PROXY).delegateInvocation(delay);
        return response(delay);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/native")
    public String invokeNativeLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /native");
        getClientByTag(TelemetryTag.TYPE_NATIVE).delegateInvocation(delay);
        return response(delay);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/timed-micrometer")
    public String invokeTimedMicrometerLatencyMeasure(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /timed-micrometer");
        getClientByTag(TelemetryTag.TYPE_TIMED).delegateInvocation(delay);
        return response(delay);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/without-measurement")
    public String invokeClientDelayService(
            @RequestParam(name = "delay", defaultValue = "1", required = false) long delay) {
        log.info("Incoming GET request for /without-measurement");
        delayService.makeDelay(delay);
        return response(delay);
    }

    private ClientDelayService getClientByTag(TelemetryTag tag) {
        return clientDelayServiceContext.getByType(ClientDelayService.constructKey(tag));
    }

    private String response(long delay) {
        return "OK after " + delay + " ms delay";
    }
}
