package com.github.jenkaby.presentation.controller.mapper;

import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.presentation.controller.model.TransactionEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionEventMapper {

    public TransactionEvent toEntity(TransactionEventRequest source) {
        return new TransactionEvent(
                source.transactionId(),
                source.status(),
                source.amount(),
                source.createdAt(),
                source.transactionTimestamp()
        );
    }
}
