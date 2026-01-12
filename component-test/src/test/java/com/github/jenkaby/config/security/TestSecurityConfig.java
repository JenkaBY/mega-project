package com.github.jenkaby.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtDecoder testJwtDecoder() {
        log.info("Initializing TestJwtDecoder for component tests");
        return new TestJwtDecoder();
    }

    @Slf4j
    private static class TestJwtDecoder implements JwtDecoder {

        private static final String VALID_JWT_TOKEN = "valid-jwt-token";
        private static final String INVALID_JWT_TOKEN = "invalid-jwt-token";

        @Override
        public Jwt decode(String token) throws JwtException {

            log.info("Decoding JWT token: {}", token);
            if (VALID_JWT_TOKEN.equals(token)) {
                log.info("Valid JWT token accepted: {}", token);
                return createMockJwt(token);
            }
            log.warn("Unknown JWT token: {}", token);
            throw new JwtException("Invalid JWT token");
        }

        private Jwt createMockJwt(String token) {
            Map<String, Object> headers = new HashMap<>();
            headers.put("alg", "none");
            headers.put("typ", "JWT");

            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", "test-user");
            claims.put("iss", "http://localhost:8080/auth/realms/test");
            claims.put("aud", List.of("component-mega-app"));
            claims.put("exp", Instant.now().plusSeconds(3600).getEpochSecond());
            claims.put("iat", Instant.now().getEpochSecond());
            claims.put("jti", "test-jwt-id");
            claims.put("scope", "read write");
            claims.put("preferred_username", "testuser");
            claims.put("email", "testuser@example.com");
            claims.put("name", "test user");
            claims.put("roles", List.of("USER", "ADMIN", "developer"));
            claims.put("upn", "authenticated@domain.com");
            claims.put("tid", "00000000-0000-0000-0000-000000000001");
            claims.put("oid", "00000000-0000-0000-0000-000000000002");
            claims.put("deviceid", "00000000-0000-0000-0000-000000000003");

            Instant issuedAt = Instant.now();
            Instant expiresAt = issuedAt.plusSeconds(3600);

            return new Jwt(
                    token,
                    issuedAt,
                    expiresAt,
                    headers,
                    claims
            );
        }
    }
}
