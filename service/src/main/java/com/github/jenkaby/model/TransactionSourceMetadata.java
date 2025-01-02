package com.github.jenkaby.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record TransactionSourceMetadata(
        String source,
        String encoding,
        Map<String, Object> headers,
        String modified
        ){
}
