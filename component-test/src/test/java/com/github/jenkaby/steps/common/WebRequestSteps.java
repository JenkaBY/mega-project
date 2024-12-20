package com.github.jenkaby.steps.common;

import com.github.jenkaby.ScenarioContext;
import com.github.jenkaby.model.JsonPathExpectation;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@Slf4j
public class WebRequestSteps {

    private final TestRestTemplate restClient;
    private final ScenarioContext scenarioContext;

    @When("a request has been made to {string} endpoint")
    public void theHealthEndpointRespondsWithOK(String endpoint) {
        scenarioContext.setResponse(restClient.getForEntity(endpoint, String.class));
    }


    @Then("the response status is {int}")
    public void theResponseStatusIs(int expectedStatus) {
        var actualStatusCode = scenarioContext.getResponse().getStatusCode();

        assertThat(actualStatusCode).isEqualTo(HttpStatusCode.valueOf(expectedStatus));
    }

    @Then("the response contains")
    public void theResponseContains(List<JsonPathExpectation> expectations) {
        var documentContext = JsonPath.parse(scenarioContext.getStringResponseBody());
        var softly = new SoftAssertions();
        expectations.forEach(exp -> {
            var jsonPath = JsonPath.compile(exp.path());
            Object actual = documentContext.read(jsonPath);
            softly.assertThat(actual).isEqualTo(exp.value());
        });
        softly.assertAll();
    }
}
