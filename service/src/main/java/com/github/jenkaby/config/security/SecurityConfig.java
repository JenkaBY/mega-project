package com.github.jenkaby.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Order(1)
    @Bean
    public SecurityFilterChain basic(HttpSecurity http) throws Exception {

        return http
                .securityMatcher("/api/v1/secured-resources/basic/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/secured-resources/basic/**").hasRole("developer");
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
//                the UserDetailsService bean is injected automatically
//                .userDetailsService(userDetailsService())
                .build();
    }

    @Order(2)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   Converter<Jwt, AbstractAuthenticationToken> authenticationConverter,
                                                   @Qualifier("customDecoderOriginal") JwtDecoder jwtDecoderOriginal
    ) throws Exception {
        log.info("JWT Decoder in SecurityConfig: {}", jwtDecoderOriginal.getClass().getName());
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/favicon.ico").permitAll();
                    auth.requestMatchers("/api/v1/secured-resources/has-roles/developer").hasRole("developer");
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

    /**
     * The bean is required for basic authz. It's automatically injected into a proper class
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("testuser-basic")
                .password("{noop}password")
                .roles("developer")
                .build();

        return new InMemoryUserDetailsManager(user) {

            @Override
            public UserDetails loadUserByUsername(String username) {
                log.info("Loading user by username: {}", username);
                return super.loadUserByUsername(username);
            }
        };
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

            List<String> roles = Optional.ofNullable((List<String>) claims.get("roles")).orElse(List.of())
                    .stream()
                    .map(roleName -> "ROLE_" + roleName)
                    .collect(Collectors.toCollection(ArrayList::new));
            authorities.addAll(roles);

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
    @Qualifier("customDecoderOriginal")
    public JwtDecoder jwtDecoder(@Autowired RestTemplate restTemplate,
                                 @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:dummy}") String issueUri,
                                 @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:dummy}") String jwkSetUri
    ) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
//                .withIssuerLocation(issueUri)
                .withJwkSetUri(jwkSetUri)
//                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .restOperations(restTemplate)
//                .jwsAlgorithms(signatureAlgorithms -> )
                .build();
//        JwtDecoderProviderConfigurationUtils::getJWSAlgorithms

        OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefaultWithIssuer(issueUri);
        jwtDecoder.setJwtValidator(jwtValidator);

        return (token) -> {
            log.info("Token to decode: {}", token);
            return jwtDecoder.decode(token);
        };
    }

    public interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
    }
}
