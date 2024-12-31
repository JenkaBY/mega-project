package com.github.jenkaby.config.messaging.kafka.topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Arrays;
import java.util.stream.Stream;

// TODO See the README.md#TODO section
@RequiredArgsConstructor
@Configuration
@Profile("createTopics")
@EnableConfigurationProperties(TopicsProperties.class)
@Slf4j
public class TopicCreationConfig {

    private final TopicsProperties topicsProperties;

    @Bean
    public KafkaAdmin.NewTopics createTopics() {
        NewTopic[] array = topicsProperties.getTopics().values().stream()
                .flatMap(creationProps -> Stream.of(createMainTopic(creationProps), createDeadLetterTopic(creationProps)))
                .toArray(NewTopic[]::new);
        log.info("The following topics will be created/updated: {}", Arrays.toString(array));
        return new KafkaAdmin.NewTopics(array);
    }

    private static NewTopic createMainTopic(TopicsProperties.TopicCreationProperties topicCreationProperties) {
        return TopicBuilder
                .name(topicCreationProperties.getName())
                .partitions(topicCreationProperties.getPartition())
                .replicas(1)
                .build();
    }

    private static NewTopic createDeadLetterTopic(TopicsProperties.TopicCreationProperties topicCreationProperties) {
        return TopicBuilder
                .name(topicCreationProperties.getDlt())
                .partitions(1)
                .replicas(1)
                .build();
    }
}
