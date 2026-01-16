package com.github.jenkaby.steps;

import com.github.jenkaby.model.MessageLogExpectation;
import com.github.jenkaby.persistance.entity.MessageLog;
import com.github.jenkaby.persistance.repository.MessageLogRepository;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@RequiredArgsConstructor
public class MessageLogSteps {

    private final MessageLogRepository repository;

    @Then("the db contains the following MessageLog records")
    public void dbContainsMessageLogService(List<MessageLogExpectation> expectedRecords) {
        var sortedExpected = expectedRecords.stream()
                .sorted(Comparator.comparing(MessageLogExpectation::messageId))
                .toList();

        await()
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .atMost(Duration.of(3, ChronoUnit.SECONDS))
                .untilAsserted(() -> {
                    var actualSorted = repository.findAll().stream()
                            .sorted(Comparator.comparing(MessageLog::getMessageId))
                            .toList();

                    log.info("[TEST] Actual MessageLog records from DB: {}. Expected size {}", actualSorted.size(), expectedRecords.size());
                    assertThat(actualSorted).hasSize(sortedExpected.size());

                    assertThat(actualSorted).zipSatisfy(sortedExpected, (actual, expected) -> {
                        log.info("[TEST] Actual: {}", actual);
                        log.info("[TEST] Expected: {}:", expected);
                        var softly = new SoftAssertions();
                        softly.assertThat(actual.getMessageId()).isEqualTo(expected.messageId());
                        softly.assertThat(actual.getEncodingType()).isEqualTo(expected.encoding());
                        softly.assertThat(actual.getStatus()).isEqualTo(expected.status());
                        softly.assertThat(actual.getSource()).contains(expected.topic());
                        softly.assertThat(actual.getModifiedBy()).contains(expected.modifiedBy());
                        softly.assertThat(actual.getRawHeader()).isNotNull();
                        softly.assertThat(actual.getRawMessage()).isNotNull();
                        softly.assertAll();
                    });
                });

    }
}
