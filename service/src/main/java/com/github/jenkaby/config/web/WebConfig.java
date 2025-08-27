package com.github.jenkaby.config.web;

import com.github.jenkaby.config.web.support.LoggingClientHttpRequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Slf4j
@AllArgsConstructor
@Configuration
public class WebConfig {

    @Value("${app.debug.rest-template.all-requests}")
    private final boolean shouldDebugRestTemplateRequests;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .additionalInterceptors(getInterceptors())
                .build();
    }

    private Collection<ClientHttpRequestInterceptor> getInterceptors() {
        var interceptors = new HashSet<ClientHttpRequestInterceptor>();
        log.info("[PROPERTY] app.debug.rest-template.all-requests={}", shouldDebugRestTemplateRequests);
        if (shouldDebugRestTemplateRequests) {
            interceptors.add(new LoggingClientHttpRequestInterceptor());
        }
        return Collections.unmodifiableSet(interceptors);
    }
}
