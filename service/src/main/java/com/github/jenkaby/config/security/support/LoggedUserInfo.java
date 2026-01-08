package com.github.jenkaby.config.security.support;

import lombok.Builder;

import java.net.URL;

@Builder
public record LoggedUserInfo(
        URL issuer,
        String id,
        String name,
        String email,
        String userName
) {
}
