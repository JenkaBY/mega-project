package com.github.jenkaby.config.messaging.kafka.topics;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "app.kafka")
public class TopicsProperties {

    private Map<String, TopicCreationProperties> topics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicCreationProperties {

        private String name;
        private String dlt;
        private int partition;
    }
}
