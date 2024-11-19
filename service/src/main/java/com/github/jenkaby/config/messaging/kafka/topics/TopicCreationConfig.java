package com.github.jenkaby.config.messaging.kafka.topics;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

// TODO See the README.md#TODO section
@RequiredArgsConstructor
@Configuration
@Profile("createTopics")
public class TopicCreationConfig {

    @Value("${app.kafka.topics.transaction.name}")
    private final String transactionTopic;

    @Value("${app.kafka.topics.transaction.dlt}")
    private final String transactionDlt;

    @Value("${app.kafka.topics.transaction.partition}")
    private final int transactionPartition;
    // notification message topic
    @Value("${app.kafka.topics.message.name}")
    private final String messageTopic;

    @Value("${app.kafka.topics.message.dlt}")
    private final String messageDlt;

    @Value("${app.kafka.topics.message.partition}")
    private final int messagePartition;


    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder
                .name(transactionTopic)
                .partitions(transactionPartition)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic transactionDlt() {
        return TopicBuilder
                .name(transactionDlt)
                .partitions(1)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic messageTopic() {
        return TopicBuilder
                .name(messageTopic)
                .partitions(messagePartition)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic messageDlt() {
        return TopicBuilder
                .name(messageDlt)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
