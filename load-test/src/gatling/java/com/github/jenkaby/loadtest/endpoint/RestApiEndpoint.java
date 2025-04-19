package com.github.jenkaby.loadtest.endpoint;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RestApiEndpoint {

    public static HttpRequestActionBuilder delayEndpoint(String exact) {
        return http("http request to the 'delay %s' endpoint".formatted(exact))
                .get("/api/delay/%s".formatted(exact))
                .queryParam("delay", 5)
                .check(status().is(200));
    }
}
