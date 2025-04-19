package com.github.jenkaby.config.telemetry;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.stream.LongStream;

@Configuration
public class TimerMetricFilterConfig implements MeterFilter {

    @Override
    public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {

        // Only configure the metric for the timer object of the observation
        if (id.getName().startsWith(TelemetryCounter.Constants.DELAY_SERVICE_LATENCY) &&
                id.getType() == Meter.Type.TIMER) {

//            var slos = LongStream.of(1, 5, 10, 50, 100, 250, 500, 1000, 2000)
//            this slos were tuned for performance testing for delay=5s
            var slos = LongStream.iterate(5_000_000, res -> res <= 7_500_000, res -> res + 50_000)
                    .mapToObj(Duration::ofNanos)
                    .map(Duration::toNanos)
                    .mapToDouble(Long::doubleValue)
                    .toArray();

            return DistributionStatisticConfig.builder()
//                    .percentilesHistogram(true) // enable histogram. Ships owns ~ 70 buckets predefined buckets from 0 to 30s
//                    .maximumExpectedValue(Long.valueOf(Duration.ofMillis(2000).toNanos()).doubleValue()) // limits number of predefined buckets
                    .serviceLevelObjectives(slos) // allows to ship custom defined buckets
                    .build()
                    .merge(config);
        }

        return MeterFilter.super.configure(id, config);
    }
}
