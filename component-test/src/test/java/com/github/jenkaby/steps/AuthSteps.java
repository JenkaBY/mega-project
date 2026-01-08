package com.github.jenkaby.steps;

import com.github.jenkaby.context.ScenarioContext;
import com.github.jenkaby.model.AuthTokenType;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j
@RequiredArgsConstructor
public class AuthSteps {

    private final ScenarioContext scenarioContext;

    @Given("the user is authenticated with {tokenType} token/username {string}")
    public void theUserIsAuthenticatedWithToken(AuthTokenType tokenType, String token) {
        log.info("Setting {} token or username: {}", tokenType, token);
        scenarioContext.replaceHeader(HttpHeaders.AUTHORIZATION, tokenType.formatHeaderValue(token));
    }

    @Given("there's no authenticated user")
    public void noAuthUser() {
        log.info("Removing authentication token");
        scenarioContext.removeHeader(HttpHeaders.AUTHORIZATION);
    }
}
