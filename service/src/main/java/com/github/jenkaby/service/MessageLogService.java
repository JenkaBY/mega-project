package com.github.jenkaby.service;

import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.model.TransactionSourceMetadata;
import com.github.jenkaby.persistance.repository.MessageLogRepository;
import com.github.jenkaby.service.mapper.MessageLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageLogService {

    private final MessageLogRepository messageLogRepository;
    private final MessageLogMapper messageLogMapper;

    public void save(TransactionEvent transactionEvent, TransactionSourceMetadata metadata) {
       var entity = messageLogMapper.toEntity(transactionEvent, metadata);
       messageLogRepository.save(entity);
    }
}
