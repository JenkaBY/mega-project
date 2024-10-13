package com.github.jenkaby.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record TransactionEvent(
        UUID transactionId,
        String status,
        BigDecimal amount,
        ZonedDateTime createdAt,
        ZonedDateTime transactionTimestamp
        ) {
}
