package com.github.jenkaby.service.support.annotation;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;
import com.github.jenkaby.service.support.ProxyType;

import java.lang.annotation.*;


/**
 * this annotation is used for recording metrics with a Bean Post Processor magic
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordMetric {

    TelemetryCounter metric();

    TelemetryTag[] tags() default {};

    ProxyType proxyType() default ProxyType.JDK_DYNAMIC;
}
