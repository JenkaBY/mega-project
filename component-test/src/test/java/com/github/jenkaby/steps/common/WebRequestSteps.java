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
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@Slf4j
public class WebRequestSteps {

    private final TestRestTemplate restClient;
    private final ScenarioContext scenarioContext;
    @LocalServerPort
    private final int port;

    @When("a {httpMethod} request has been made to {string} endpoint")
    public void requestHasBeenMadeToEndpoint(HttpMethod method, String endpoint) {
        requestHasBeenMadeToEndpointTimes(method, 1, endpoint, null);
    }

    @When("a {httpMethod} request has been made to {string} endpoint with query parameters")
    public void requestHasBeenMadeToEndpoint(HttpMethod method,
                                             String endpoint,
                                             @Transpose DataTable queryParams) {
        requestHasBeenMadeToEndpointTimes(method, 1, endpoint, queryParams);
    }

    @When("a {httpMethod} request has been made {int} times to {string} endpoint with query parameters")
    public void requestHasBeenMadeToEndpointTimes(HttpMethod method,
                                                  int times,
                                                  String endpoint,
                                                  @Nullable @Transpose DataTable queryParams) {

        var request = new HttpEntity<>(scenarioContext.getRequestBody(), HttpHeaders.readOnlyHttpHeaders(scenarioContext.getRequestHeaders()));
        var uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:" + port + endpoint);
        Optional.ofNullable(queryParams)
                .map(DataTable::asMap)
                .orElse(Map.of())
                .forEach(uriBuilder::queryParam);
        var uri = uriBuilder.build().toUri();
        var response = IntStream.range(0, times).mapToObj(i -> restClient.exchange(uri, method, request, String.class))
                .peek(resp -> log.info("Response : {}", resp))
                .toList().getLast();
        log.debug("Last Response : {}", response);
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
