package com.github.jenkaby.steps.common;

import com.github.jenkaby.service.support.ProxyType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AllArgsConstructor
public class ApplicationSteps {

    private final ApplicationContext applicationContext;

    @Given("application is started")
    public void applicationIsStarted() {
//        do nothing
    }

    @SneakyThrows
    @Then("the {string} bean of the {string} class in the application context is {proxyType} proxy type")
    public void theBeanInTheApplicationContextIsProxyTypeProxyType(String beanName, String fullClass, ProxyType expected) {
        Class<?> classToCast = Class.forName(fullClass);
        var actual = applicationContext.getBean(beanName, classToCast);
        log.info("A current class of the {} bean is {}", beanName, actual.getClass());
        assertThat(ProxyType.fromClass(actual.getClass())).isEqualTo(expected);

    }
}
