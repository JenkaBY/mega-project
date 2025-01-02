package com.github.jenkaby.presentation.controller.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

public record TransactionEventRequest(
        UUID transactionId,
        String status,
        BigDecimal amount,
        Instant createdAt,
        Instant transactionTimestamp
) {
}
