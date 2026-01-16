package com.github.jenkaby.config.web.support;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public @NonNull ClientHttpResponse intercept(HttpRequest request, byte @NonNull [] body, ClientHttpRequestExecution execution) throws IOException {

        log.info("DOWNSTREAM REQUEST -> method={} uri={} headers={} body={}",
                request.getMethod(),
                request.getURI(),
                request.getHeaders(),
                new String(body, StandardCharsets.UTF_8));

        ClientHttpResponse response = execution.execute(request, body);

        String responseBody = new BufferedReader(
                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        log.info("DOWNSTREAM RESPONSE -> status={} headers={} body={}",
                response.getStatusCode(),
                response.getHeaders(),
                responseBody);

        return response;
    }
}
