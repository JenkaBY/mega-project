package com.github.jenkaby.persistance.repository;

import com.github.jenkaby.persistance.entity.MessageLog;
import com.github.jenkaby.support.db.AbstractReusableDbTest;
import com.github.jenkaby.support.db.annotation.DbTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DbTest
class MessageLogRepositoryTest extends AbstractReusableDbTest {

    @Autowired
    private MessageLogRepository underTest;

    @Test
    void save_Should_CreateARecord_When_SaveIsInvoked() {
        var messageId = UUID.randomUUID();
        var object = getMessageLog(messageId);

        var actual = underTest.save(object);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getMessageId()).isEqualTo(messageId);
        assertThat(actual.getEncodingType()).isEqualTo("avro");
        assertThat(actual.getSource()).isEqualTo(this.getClass().getName());
        assertThat(actual.getRawHeader()).contains(List.of("{", "header", ":", "testValue", "}"));
        assertThat(actual.getRawMessage()).contains(List.of("{", "payloadField", ":", "1", "}"));
        assertThat(actual.getStatus()).isEqualTo("TEST_STATUS");
    }

    private MessageLog getMessageLog(UUID messageId) {
        var object = new MessageLog();
        object.setMessageId(messageId);
        object.setStatus("TEST_STATUS");
        object.setEncodingType("avro");
        object.setSource(this.getClass().getName());
        object.setRawHeader(
                """
                        {
                        "header": "testValue"
                        }
                        """
        );
        object.setRawMessage(
                """
                        {
                            "payloadField": 1
                        }
                        """);
        object.setModifiedBy("TEST_USER");
        return object;
    }
}