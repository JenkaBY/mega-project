package com.github.jenkaby.model;

import com.github.jenkaby.messaging.base.JsonPayload;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

public record TransactionEvent(
        UUID transactionId,
        String status,
        BigDecimal amount,
        Instant createdAt,
        Instant transactionTimestamp
) implements JsonPayload {
}
