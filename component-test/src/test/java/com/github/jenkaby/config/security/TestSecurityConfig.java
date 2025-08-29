package com.github.jenkaby.config.security;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TestSecurityConfig {

    @Value("${keycloak.auth-server-url}")
    private final String keycloakUrl;

    @Value("${keycloak.realm}")
    private final String realm;

    @Value("${keycloak.resource}")
    private final String clientId;

    @Value("${keycloak.credentials.secret}")
    private final String clientSecret;


    @Bean
    public KeycloakContainer keyCloakContainer(DynamicPropertyRegistry registry) {
        KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:26.3")
                .withRealmImportFile("/keycloak/realms/local-test-realm.json");
        keycloakContainer.start();

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/" + realm);
//        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/" + realm + "/.well-known/openid-configuration");
        registry.add("keycloak.auth-server-url", () -> keycloakContainer.getAuthServerUrl());

        log.info("Keycloak HOST: {}", keycloakContainer.getHost());
        log.info("keyCloakContainer.getAuthServerUrl() : {}", keycloakContainer.getAuthServerUrl());
        log.info("keyCloakContainer.getPort() : {}", keycloakContainer.getFirstMappedPort());
        return keycloakContainer;
    }

    @Bean
    public KeycloakBuilder keycloakClientBuilder(KeycloakContainer keyCloakContainer) {
        return KeycloakBuilder.builder()
                .serverUrl(keyCloakContainer.getAuthServerUrl())
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.PASSWORD);
    }
}
