package com.github.jenkaby.service.mapper;

import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.model.TransactionSourceMetadata;
import com.github.jenkaby.persistance.entity.MessageLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;


@Mapper(uses = {RawDataMapper.class}, imports = {Instant.class})
public abstract class MessageLogMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(source = "source.transactionId", target = "messageId")
    @Mapping(source = "source", target = "rawMessage")
    @Mapping(source = "metadata.encoding", target = "encodingType")
    @Mapping(source = "metadata.modified", target = "modifiedBy")
    @Mapping(source = "metadata.headers", target = "rawHeader")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    public abstract MessageLog toEntity(TransactionEvent source, TransactionSourceMetadata metadata);
}
