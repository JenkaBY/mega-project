package com.github.jenkaby.config.telemetry;

import io.micrometer.core.instrument.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum TelemetryTag {
    TYPE_AOP_ANNOTATION(Constants.AOP_ANNOTATION_TAGS),
    TYPE_AOP_EXECUTION(Constants.AOP_AROUND_EXECUTION_TAGS),
    TYPE_BPP_DYNAMIC_PROXY(Constants.BPP_DYNAMIC_TAGS),
    TYPE_BPP_CGLIB_PROXY(Constants.BPP_CGLIB_TAGS),
    TYPE_TIMED(Constants.TIMED_TAGS),
    TYPE_NATIVE(Constants.NATIVE_TAGS);

    private final Tag[] tags;

    public static class Constants {
        public static final Tag[] TIMED_TAGS = {Tag.of("type", "timed")};
        public static final Tag[] NATIVE_TAGS = {Tag.of("type", "native")};
        public static final Tag[] BPP_DYNAMIC_TAGS = {Tag.of("type", "bpp-dynamic")};
        public static final Tag[] BPP_CGLIB_TAGS = {Tag.of("type", "bpp-cglib")};
        public static final Tag[] AOP_AROUND_EXECUTION_TAGS = {Tag.of("type", "aop-execution")};
        public static final Tag[] AOP_ANNOTATION_TAGS = {Tag.of("type", "aop-annotation")};
    }
}
