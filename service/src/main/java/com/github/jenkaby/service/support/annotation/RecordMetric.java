package com.github.jenkaby.service.support.annotation;

import com.github.jenkaby.config.telemetry.TelemetryCounter;
import com.github.jenkaby.config.telemetry.TelemetryTag;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordMetric {

    TelemetryCounter metric();

    TelemetryTag[] tags() default {};
}
