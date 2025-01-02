package com.github.jenkaby.transformer;

import com.github.jenkaby.model.Constant;
import com.github.jenkaby.presentation.controller.model.TransactionEventRequest;
import io.cucumber.java.DataTableType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class TransactionEventDtoTransformer {

    @DataTableType
    public TransactionEventRequest transform(Map<String, String> table) {
        return new TransactionEventRequest(
                Constant.getTxnId(table),
                table.getOrDefault("status", "NEW"),
                new BigDecimal(table.get("amount")),
                Optional.ofNullable(table.get("createdAt")).map(Instant::parse).orElse(Instant.now()),
                Optional.ofNullable(table.get("txnTs")).map(Instant::parse).orElse(Instant.now())
        );
    }
}
