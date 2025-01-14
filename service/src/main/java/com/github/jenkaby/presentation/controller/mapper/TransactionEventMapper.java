package com.github.jenkaby.presentation.controller.mapper;

import com.github.jenkaby.megaapp.avro.payload.v0.TransactionEventAvro;
import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.presentation.controller.model.TransactionEventRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TransactionEventMapper {

    TransactionEvent toEntity(TransactionEventRequest source);

    TransactionEvent toEntity(TransactionEventAvro source);

    @Mapping(source = "eventSource", target = "source")
    TransactionEventAvro toAvro(TransactionEventRequest source, String eventSource);
}
