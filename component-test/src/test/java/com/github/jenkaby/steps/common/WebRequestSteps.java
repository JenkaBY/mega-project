package com.github.jenkaby.steps.common;

import com.github.jenkaby.context.ScenarioContext;
import com.github.jenkaby.model.JsonPathExpectation;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@Slf4j
public class WebRequestSteps {

    private final TestRestTemplate restClient;
    private final ScenarioContext scenarioContext;
    @LocalServerPort
    private final int port;

    @When("a request has been made to {string} endpoint")
    public void requestHasBeenMadeToEndpoint(String endpoint) {
        log.info("++TxN is active: {}", TransactionSynchronizationManager.isActualTransactionActive());
        scenarioContext.setResponse(restClient.getForEntity(endpoint, String.class));
    }

    @When("a {httpMethod} request has been made to {string} endpoint with query parameters")
    public void requestHasBeenMadeToEndpoint(HttpMethod method,
                                             String endpoint,
                                             @Transpose DataTable queryParams) {

        var request = new HttpEntity<>(scenarioContext.getRequestBody());
        var uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + endpoint);
        queryParams.asMap().forEach(uriBuilder::queryParam);
        var uri = uriBuilder.build().toUri();
        var response = restClient.exchange(uri, method, request, String.class);
        log.info("Response : {}", response);
        scenarioContext.setResponse(response);
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
