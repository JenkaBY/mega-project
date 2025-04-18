package com.github.jenkaby.steps;

import com.github.jenkaby.config.TestPropertiesConfig;
import io.cucumber.java.en.Then;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Offset;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@RequiredArgsConstructor
public class MetricsStep {

    private final MeterRegistry registry;
    private final TestPropertiesConfig.LatencyProperties latencyTestConfig;

    @Then("measured average {string} metric has been recorded at ~{long} ms with {tags} tags")
    public void measuredLatencyMetricHasRecordedDelayMs(String metric, long expectedLatencyMs, List<Tag> tags) {
        if (metric.isBlank()) {
            return;
        }
        long thresholdConfigMs = latencyTestConfig.getValidationThreshold().toMillis();
        log.info("THRESHOLD {} ms", thresholdConfigMs);
        var thresholdMs = Offset.offset((double) thresholdConfigMs);
        await().atMost(Duration.ofMillis(1_000)).untilAsserted(() -> {
            var expectedMetric = registry.find(metric).tags(tags).timer();
            debugMetrics();
            assertThat(expectedMetric).isNotNull();
            assertThat(expectedMetric.mean(TimeUnit.MILLISECONDS)).isCloseTo(expectedLatencyMs, thresholdMs);
        });
    }

    private void debugMetrics() {
        if (!log.isDebugEnabled()) {
            return;
        }
        registry.getMeters().stream()
                .sorted(Comparator.comparing((m) -> m.getId().getName()))
                .forEach(m -> log.debug("Metric[{}]: {} tags: {}", m.getId().getType(), m.getId().getName(), m.getId().getTags()));
    }
}
