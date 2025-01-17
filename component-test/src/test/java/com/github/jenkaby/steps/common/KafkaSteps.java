package com.github.jenkaby.steps.common;

import com.github.jenkaby.context.LocalMessagesStore;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@RequiredArgsConstructor
public class KafkaSteps {

    private final LocalMessagesStore messagesStore;

    @Then("{int} message(s) was/were received on the {string} topic")
    public void verifyNumberOfMessagesReceivedOnTheTopic(int expectedNumberOfMsg, String topicName) {
        await()
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .atMost(Duration.of(3, ChronoUnit.SECONDS))
                .untilAsserted(() -> {
                    List<LocalMessagesStore.Message<String>> messages = messagesStore.getMessagesOnTopic(topicName, String.class);
                    log.info("[TEST] ++++  messages : {}", messages);
                    assertThat(messages).hasSize(expectedNumberOfMsg);
                });
    }
}
