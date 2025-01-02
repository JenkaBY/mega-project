package com.github.jenkaby.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MessageLogExpectation(
        UUID messageId,
        String status,
        String topic,
        String modifiedBy,
        String encoding
) {
}
