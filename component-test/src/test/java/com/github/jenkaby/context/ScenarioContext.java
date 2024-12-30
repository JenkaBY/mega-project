package com.github.jenkaby.context;


import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

@Getter
@Setter
@ScenarioScope
@Component
public class ScenarioContext {

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
}
