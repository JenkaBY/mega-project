package com.github.jenkaby.steps;

import com.github.jenkaby.model.TransactionEvent;
import com.github.jenkaby.service.TransactionJsonListenerService;
import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
public class MockSteps {

    private final TransactionJsonListenerService transactionJsonListenerService;

    @After
    public void resetMock() {
        Mockito.reset(transactionJsonListenerService);
    }

    @Then("verify TransactionJsonListenerService was invoked {int} time(s)")
    @SuppressWarnings("unchecked")
    public void verifyTransactionLogServiceWasInvokedNTimes(int times) {
        verify(transactionJsonListenerService, times(times))
                .process(anyMap(), any(TransactionEvent.class), anyString(), any(ConsumerRecord.class));
    }

    @Then("verify no interaction with TransactionJsonListenerService happened during {int} seconds")
    @SuppressWarnings("unchecked")
    public void verifyNoInteractionTransactionLogService(int seconds) {
        var duration = Duration.of(seconds, ChronoUnit.SECONDS);
        int pollInterval = 100;
        await()
                .pollInterval(pollInterval, TimeUnit.MILLISECONDS)
                .during(duration)
                .atMost(duration.plusMillis(pollInterval * 2))
                .untilAsserted(() -> verify(transactionJsonListenerService, never())
                        .process(anyMap(), any(TransactionEvent.class), anyString(), any(ConsumerRecord.class)));
    }
}
