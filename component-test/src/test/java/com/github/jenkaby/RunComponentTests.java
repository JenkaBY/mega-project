package com.github.jenkaby;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;


@Suite
@SelectClasspathResource("features")
// TODO move the glue code to separate package
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.github.jenkaby")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @skip")
// uncomment the line below. comment out the line above and mark @run the test to be executed
//@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@run")
public class RunComponentTests {

    //   TODO try to create topic during container creation
    @ActiveProfiles({"test", "createTopics"})
    @SpringBootTest(classes = MegaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @CucumberContextConfiguration
    public static class ApplicationContextTestConfiguration {

        @ServiceConnection
        public final static PostgreSQLContainer<?> postgresContainer
                = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14-alpine"));

        @ServiceConnection
//        TODO replace with ConfluentKafkaContainer
        public static KafkaContainer kafka
//                Must be aligned with being used in lib.versions.toml
                = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"));


        @DynamicPropertySource
        static void registerKafkaProperties(DynamicPropertyRegistry registry) {
            registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers());
        }
    }
}
