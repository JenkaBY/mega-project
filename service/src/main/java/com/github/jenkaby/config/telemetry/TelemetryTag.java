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
        public static final String TYPE = "type";
        public static final String TIMED_MICROMETER = "timed-micrometer";
        public static final String NATIVE = "native";
        public static final String BPP_DYNAMIC = "bpp-dynamic";
        public static final String BPP_CGLIB = "bpp-cglib";
        public static final String AOP_EXECUTION = "aop-execution";
        public static final String AOP_ANNOTATION = "aop-annotation";

        public static final Tag[] TIMED_TAGS = {Tag.of(TYPE, TIMED_MICROMETER)};
        public static final Tag[] NATIVE_TAGS = {Tag.of(TYPE, NATIVE)};
        public static final Tag[] BPP_DYNAMIC_TAGS = {Tag.of(TYPE, BPP_DYNAMIC)};
        public static final Tag[] BPP_CGLIB_TAGS = {Tag.of(TYPE, BPP_CGLIB)};
        public static final Tag[] AOP_AROUND_EXECUTION_TAGS = {Tag.of(TYPE, AOP_EXECUTION)};
        public static final Tag[] AOP_ANNOTATION_TAGS = {Tag.of(TYPE, AOP_ANNOTATION)};
    }
}
