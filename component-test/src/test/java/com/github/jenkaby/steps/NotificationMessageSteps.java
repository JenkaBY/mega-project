package com.github.jenkaby.steps;

import com.github.jenkaby.context.LocalMessagesStore;
import com.github.jenkaby.model.ReceivedNotificationMessage;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Value;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class NotificationMessageSteps {

    private final LocalMessagesStore messagesStore;
    @Value("${app.kafka.topics.message.name}")
    private final String notificationTopic;

    @Then("the received notification message(s) is/are")
    public void theReceivedNotificationMessageIs(List<ReceivedNotificationMessage> expectedMsg) {
        var sortedExpected = expectedMsg.stream()
                .sorted(Comparator.comparing(ReceivedNotificationMessage::messageKey))
                .toList();

        var actualSorted = messagesStore.getMessagesOnTopic(notificationTopic, String.class).stream()
                .sorted(Comparator.comparing(LocalMessagesStore.Message::key))
                .toList();

        assertThat(actualSorted)
                .zipSatisfy(sortedExpected,
                        (actual, expected) -> {
                            SoftAssertions softly = new SoftAssertions();
                            softly.assertThat(actual.value()).isEqualTo(expected.payload());
                            softly.assertThat(actual.key()).isEqualTo(expected.messageKey());
                            softly.assertAll();
                        });
    }
}
