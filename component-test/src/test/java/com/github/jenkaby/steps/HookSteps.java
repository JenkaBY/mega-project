package com.github.jenkaby.steps;

import com.github.jenkaby.RunComponentTests;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HookSteps {

    @BeforeAll
    public static void setUp() {
        log.info("Starting container...");
        RunComponentTests.ApplicationContextTestConfiguration.postgresContainer.start();
    }


    @AfterAll
    public static void tearDown() {
        if (RunComponentTests.ApplicationContextTestConfiguration.postgresContainer.isRunning()) {
            log.info("Releasing resources...");
            RunComponentTests.ApplicationContextTestConfiguration.postgresContainer.stop();
        }

    }
}