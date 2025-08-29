package com.github.jenkaby.steps;

import com.github.jenkaby.context.ScenarioContext;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class AuthSteps {

    private static final Map<String, String> CREDS = Map.of(
            "testuser-bearer", "password",
            "testuser-basic", "password"
    );
    private final KeycloakBuilder keycloakClientBuilder;
    private final ScenarioContext scenarioContext;

    @Given("the {string} is authenticated")
    public void theUserIsAuthenticated(String username) {
        log.info("Requesting access token for user: {}", username);
        if (username.contains("basic")) {
            String token = "%s:%s".formatted(username, CREDS.getOrDefault(username, "WRONG_PASSWORD"));
            String basicToken = new String(Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8).intern();
            log.info("Encoded base 64 token: {}", basicToken);
            scenarioContext.addHeader(HttpHeaders.AUTHORIZATION, "Basic %s".formatted(basicToken));
            return;
        }
        try (Keycloak client = buildKeyCloak(username, CREDS.get(username))) {
            var accessTokenResponse = client.tokenManager().getAccessToken();
            log.info("Obtained access token: type:{}, other Claims: {},", accessTokenResponse.getTokenType(), accessTokenResponse.getOtherClaims());
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
