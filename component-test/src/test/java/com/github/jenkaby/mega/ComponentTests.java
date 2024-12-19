package com.github.jenkaby.mega;

import com.github.jenkaby.MegaApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.jenkaby.mega.config.db.DbProperties.POSTGRES_IMAGE_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;


@Suite
@IncludeEngines("cucumber")
//@SelectPackages("com.github.jenkaby.mega")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
//@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "usage")
//@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.github.jenkaby.mega")
public class ComponentTests {

    @ActiveProfiles("test")
    @Testcontainers
    @CucumberContextConfiguration
    @SpringBootTest(classes = MegaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    public static class Bootstrap {

        @ServiceConnection
        @Container
        public static PostgreSQLContainer<?> postgres;

        static {
            System.out.println("GOAT");
            postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);
            postgres.start();
        }
    }
}
