package com.github.jenkaby;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;


@Suite
@SelectClasspathResource("features")
// TODO move the glue code to separate package
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.github.jenkaby")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @skip")
public class RunComponentTests {


    @ActiveProfiles("test")
    @SpringBootTest(classes = MegaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @CucumberContextConfiguration
    public static class ApplicationContextTestConfiguration {

        @ServiceConnection
        public final static PostgreSQLContainer<?> postgresContainer
                = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14-alpine"));


    }
}
