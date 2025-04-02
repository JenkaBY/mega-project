package com.github.jenkaby.service.delay;

import com.github.jenkaby.config.telemetry.TelemetryTag;

import java.util.Arrays;

public interface ClientDelayService {

    void delegateInvocation(long delayMs);

    String getImplementationType();

    static String constructKey(TelemetryTag tag) {
        return Arrays.toString(tag.getTags());
    }

}
