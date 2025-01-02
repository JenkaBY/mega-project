package com.github.jenkaby.presentation.controller.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record TransactionEventRequest(
        UUID transactionId,
        String status,
        BigDecimal amount,
        ZonedDateTime createdAt,
        ZonedDateTime transactionTimestamp
) {
}
