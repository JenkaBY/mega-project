package com.github.jenkaby.config.security.support;

import lombok.Builder;

import java.net.URL;
import java.util.UUID;

@Builder
public record LoggedUserInfo(
        URL issuer,
        UUID id,
        String name,
        String email,
        String userName
) {
}
