package com.github.jenkaby.mega.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class E2EApplicationSteps {

    private final ApplicationContext ctx;

    @Given("the 'component-test' module uses spring-boot-test-starter")
    public void theEEModuleUsesSpringBootTestStarter() {
        assertThat(true).isTrue();
    }

    @Then("application name should be {string}")
    public void applicationNameShouldBe(String expectedApplicationName) {
        var actual = ctx.getEnvironment().getProperty("spring.application.name");
        System.out.println("!!! THEN");
        assertThat(actual).isEqualTo(expectedApplicationName);
    }
}
