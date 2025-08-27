package com.github.jenkaby.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   Converter<Jwt, AbstractAuthenticationToken> authenticationConverter,
                                                   JwtDecoder jwtDecoderOriginal) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/token").permitAll();
                    auth.requestMatchers("/api/v1/secured-resources/has-roles/developer").hasRole("developer");
                    auth.requestMatchers("/api/v1/secured-resources/has-roles/does-not-exist").hasAnyRole("TEST");
                    auth.requestMatchers("/api/v1/secured-resources/**").authenticated();
                    auth.anyRequest().anonymous();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(resourceServer -> {
                    resourceServer.jwt(jwtConfigurer ->
                            jwtConfigurer
                                    .decoder(jwtDecoderOriginal)
                                    .jwtAuthenticationConverter(authenticationConverter)
                    );
                })
                .build();
    }

    @Bean
    JwtAuthenticationConverter authenticationConverter(AuthoritiesConverter realmRolesAuthoritiesConverter) {
        var authenticationConverter = new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> realmRolesAuthoritiesConverter.convert(jwt.getClaims()));
        return authenticationConverter;
    }

    @Bean
    AuthoritiesConverter realmRolesAuthoritiesConverter() {
        return claims -> {
            log.info("CLAIMS {}", claims);
            var realmAccess = Optional.ofNullable((Map<String, Object>) claims.get("realm_access"));
            List<String> authorities = realmAccess.flatMap(map -> Optional.ofNullable((List<String>) map.get("roles")))
                    .stream()
                    .flatMap(Collection::stream)
                    .map(roleName -> "ROLE_" + roleName)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<String> scopes = Arrays.stream(((String) claims.getOrDefault("scope", "")).split(" "))
                    .map(scope -> "SCOPE_" + scope)
                    .toList();
            authorities.addAll(scopes);

            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
        };
    }

    @Bean
    public JwtDecoder jwtDecoder(@Autowired RestTemplate restTemplate,
                                 @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issueUri
//                                 @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri
    ) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withIssuerLocation(issueUri)
//                .withJwkSetUri(jwkSetUri)
                .restOperations(restTemplate)
                .build();
        OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefaultWithIssuer(issueUri);
        jwtDecoder.setJwtValidator(jwtValidator);
        return jwtDecoder;
    }

    public interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
    }
}
