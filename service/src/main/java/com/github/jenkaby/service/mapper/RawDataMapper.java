package com.github.jenkaby.service.mapper;

import com.github.jenkaby.model.TransactionEvent;
import lombok.SneakyThrows;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Mapper
public abstract class RawDataMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    public String toJson(TransactionEvent source) {
        return objectMapper.writeValueAsString(source);
    }

    @SneakyThrows
    public String toJson(Map<String, Object> source) {
        return objectMapper.writeValueAsString(source);
    }
}
