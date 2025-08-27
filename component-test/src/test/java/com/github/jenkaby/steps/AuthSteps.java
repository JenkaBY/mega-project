package com.github.jenkaby.steps;

import com.github.jenkaby.context.ScenarioContext;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class AuthSteps {

    private static final Map<String, String> CREDS = Map.of("testuser-bdd", "password");
    private final KeycloakBuilder keycloakClientBuilder;
    private final ScenarioContext scenarioContext;

    @Given("the {string} is authenticated")
    public void theUserIsAuthenticated(String username) {
        log.info("Requesting access token for user: {}", username);
        try (Keycloak client = buildKeyCloak(username, CREDS.get(username))) {
            var accessTokenResponse = client.tokenManager().getAccessToken();
            log.info("Obtained access token: {}, {},", accessTokenResponse.getScope(), accessTokenResponse.getOtherClaims());
            scenarioContext.addHeader(HttpHeaders.AUTHORIZATION, "%s %s".formatted(accessTokenResponse.getTokenType(), accessTokenResponse.getToken()));
        }
    }

    @Given("there's no authenticated user")
    public void noAuthUser() {
        scenarioContext.removeHeader(HttpHeaders.AUTHORIZATION);
    }

    private Keycloak buildKeyCloak(String username, String password) {
        return keycloakClientBuilder
                .username(username)
                .password(password)
                .build();
    }
}
