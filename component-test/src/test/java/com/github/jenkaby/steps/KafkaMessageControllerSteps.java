package com.github.jenkaby.steps;

import com.github.jenkaby.context.ScenarioContext;
import com.github.jenkaby.presentation.controller.model.TransactionEventRequest;
import io.cucumber.java.en.And;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KafkaMessageControllerSteps {

    private final ScenarioContext scenarioContext;

    @And("the following TransactionEvent payload is")
    public void theFollowingTransactionEventPayload(TransactionEventRequest payload) {
        log.info("Payload {}", payload);
        scenarioContext.setRequestBody(payload);
    }
}
