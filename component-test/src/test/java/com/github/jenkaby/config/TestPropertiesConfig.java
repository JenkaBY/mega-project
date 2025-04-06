package com.github.jenkaby.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.time.Duration;

@Configuration
public class TestPropertiesConfig {

//    private Latency latency;

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "app.test.latency")
    public static class LatencyProperties {

        @NonNull
        private Duration validationThreshold;

    }
}
