package com.github.jenkaby.model;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public enum AuthTokenType {

    BEARER("Bearer"),
    BASIC("Basic") {
        @Override
        public String formatHeaderValue(String username) {
            String token = "%s:%s".formatted(username, "password");
            log.info("Raw basic token: {}", token);
            String basicToken = new String(Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8).intern();
            log.info("Encoded base 64 token: {}", basicToken);
            return super.formatHeaderValue(basicToken.trim());
        }
    };

    private final String prefix;

    AuthTokenType(String prefix) {
        this.prefix = prefix;
    }

    public String formatHeaderValue(String token) {
        return "%s %s".formatted(prefix, token);
    }

    public static AuthTokenType of(String type) {
        return AuthTokenType.valueOf(type.toUpperCase());
    }
}
