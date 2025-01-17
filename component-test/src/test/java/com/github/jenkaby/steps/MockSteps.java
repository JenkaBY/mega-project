package com.github.jenkaby.steps;

import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.service.TransactionJsonListenerService;
import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor
public class MockSteps {

    private final TransactionJsonListenerService transactionJsonListenerService;

    @After
    public void resetMock() {
        Mockito.reset(transactionJsonListenerService);
    }

    @Then("verify TransactionJsonListenerService was invoked {int} time(s)")
    public void verifyTransactionLogServiceWasInvokedNTimes(int times) {
        verify(transactionJsonListenerService, times(times))
                .process(anyMap(), any(TransactionEvent.class), anyString(), any(ConsumerRecord.class));
    }
}
