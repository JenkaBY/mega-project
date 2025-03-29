package com.github.jenkaby.config.telemetry;

import io.micrometer.core.instrument.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum TelemetryTag {
    TYPE_AOP_ANNOTATION(new Tag[]{Tag.of("type", "aop-annotation")}),
    TYPE_AOP_EXECUTION(new Tag[]{Tag.of("type", "aop-execution")}),
    TYPE_BPP(new Tag[]{Tag.of("type", "bpp")}),
    TYPE_NATIVE(new Tag[]{Tag.of("type", "native")});

    private final Tag[] tags;

}
