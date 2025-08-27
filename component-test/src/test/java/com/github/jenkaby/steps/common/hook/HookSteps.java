package com.github.jenkaby.steps.common.hook;

import com.github.jenkaby.RunComponentTests;
import com.github.jenkaby.context.LocalMessagesStore;
import com.github.jenkaby.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Slf4j
public class HookSteps {

    private final LocalMessagesStore store;
    private final MeterRegistry meterRegistry;
    private final ScenarioContext scenarioContext;

    @After
    public void clearMeterRegistry() {
        log.info("Clearing MeterRegistry");
        meterRegistry.clear();
    }

    @BeforeAll
    public static void setUp() {
        log.info("Starting container...");
        RunComponentTests.ApplicationContextTestConfiguration.postgresContainer.start();
        RunComponentTests.ApplicationContextTestConfiguration.kafka.start();
        speedUpAwaitility();
    }


    @AfterAll
    public static void tearDown() {
        resetAwaitility();
        if (RunComponentTests.ApplicationContextTestConfiguration.postgresContainer.isRunning()) {
            log.info("Releasing Postgres resources...");
            RunComponentTests.ApplicationContextTestConfiguration.postgresContainer.stop();
        }

        if (RunComponentTests.ApplicationContextTestConfiguration.kafka.isRunning()) {
            log.info("Releasing Kafka resources...");
            RunComponentTests.ApplicationContextTestConfiguration.kafka.stop();
        }
    }

    @After
    public void clearScenarioRecourses() {
        log.info("Clearing scenario resources...");
        store.clear();
        scenarioContext.clearHeaders();
    }

    private static void speedUpAwaitility() {
        Awaitility.setDefaultTimeout(1, TimeUnit.SECONDS);
        Awaitility.setDefaultPollInterval(50, TimeUnit.MILLISECONDS);
        Awaitility.setDefaultPollDelay(30, TimeUnit.MILLISECONDS);
    }

    private static void resetAwaitility() {
        Awaitility.await();
    }
}