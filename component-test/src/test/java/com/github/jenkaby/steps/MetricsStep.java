package com.github.jenkaby.steps;

import io.cucumber.java.en.Then;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import org.assertj.core.data.Offset;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class MetricsStep {

    private final MeterRegistry registry;

    @Then("measured {string} metric has been recorded at ~{long} ms with {tags} tags")
    public void measuredLatencyMetricHasRecordedDelayMs(String metric, long expectedLatencyMs, List<Tag> tags) {
        if (metric.isBlank()) {
            return;
        }
        var expectedMetric = registry.find(metric).tags(tags).timer();
        assertThat(expectedMetric).isNotNull();
        var twoMs = Offset.offset(2d);
        assertThat(expectedMetric.totalTime(TimeUnit.MILLISECONDS)).isCloseTo(expectedLatencyMs, twoMs);
    }
}
