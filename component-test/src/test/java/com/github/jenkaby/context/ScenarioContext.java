package com.github.jenkaby.context;


import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ScenarioScope
@Component
public class ScenarioContext {

    private final Map<String, List<String>> requestHeaders = new HashMap<>();
    private ResponseEntity<String> response;
    private TransactionStatus transactionStatus;
    private Object requestBody;

    @SuppressWarnings("unchecked")
    public <T> T getResponseBody(Class<T> clazz) {
        return (T) response.getBody();
    }

    public String getStringResponseBody() {
        return this.getResponseBody(String.class);
    }

    public void addHeader(String headerKey, String headerValue) {
        List<String> values = this.requestHeaders.getOrDefault(headerKey, new ArrayList<>());
        values.add(headerValue);
        this.requestHeaders.put(headerKey, values);
    }

    public void removeHeader(String headerKey) {
        this.requestHeaders.remove(headerKey);
    }

    public void clearHeaders() {
        this.requestHeaders.clear();
    }

    public MultiValueMap<String, String> getRequestHeaders() {
        return CollectionUtils.toMultiValueMap(requestHeaders);
    }
}
