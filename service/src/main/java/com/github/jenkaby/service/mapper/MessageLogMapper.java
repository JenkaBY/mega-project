package com.github.jenkaby.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.model.TransactionSourceMetadata;
import com.github.jenkaby.persistance.entity.MessageLog;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Component
public class MessageLogMapper {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public MessageLog toEntity(TransactionEvent source, TransactionSourceMetadata metadata) {
        var target = new MessageLog();
        target.setMessageId(source.transactionId());
        target.setRawMessage(objectMapper.writeValueAsString(source));
        target.setStatus(source.status());
//        metadata
        target.setEncodingType(metadata.encoding());
        target.setModifiedBy(metadata.modified());
        target.setSource(metadata.source());
        target.setRawHeader(objectMapper.writeValueAsString(metadata.headers()));
        target.setCreatedAt(Instant.now());
        return target;
    }
}
