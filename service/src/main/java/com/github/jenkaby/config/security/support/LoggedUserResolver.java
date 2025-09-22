package com.github.jenkaby.config.security.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;
import java.util.UUID;


@Slf4j
public class LoggedUserResolver implements HandlerMethodArgumentResolver {

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoggedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        return Optional.ofNullable(securityContextHolderStrategy.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)
                .map(p -> (Jwt) p)
                .map(jwt -> LoggedUserInfo.builder()
                        .issuer(jwt.getIssuer())
                        .id(UUID.fromString(jwt.getClaimAsString("sub")))
                        .email(jwt.getClaimAsString("email"))
                        .name(jwt.getClaimAsString("name"))
                        .userName(jwt.getClaimAsString("preferred_username"))
                        .build())
                .orElse(null);
    }
}
