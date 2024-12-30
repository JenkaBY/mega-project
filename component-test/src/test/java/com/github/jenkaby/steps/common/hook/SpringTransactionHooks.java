package com.github.jenkaby.steps.common.hook;

import com.github.jenkaby.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@RequiredArgsConstructor
public class SpringTransactionHooks {

    private final BeanFactory beanFactory;
    private final ScenarioContext scenarioContext;

    @Before(value = "@txn", order = 100)
    public void startTransaction() {
        log.info("Begin scenario Txn");
        scenarioContext.setTransactionStatus(obtainPlatformTransactionManager()
                .getTransaction(new DefaultTransactionDefinition()));
    }

    @After(value = "@txn", order = 100)
    public void rollBackTransaction() {
        log.info("Rollback scenario Txn");
        obtainPlatformTransactionManager()
                .rollback(scenarioContext.getTransactionStatus());
    }

    private PlatformTransactionManager obtainPlatformTransactionManager() {
        return beanFactory.getBean(PlatformTransactionManager.class);
    }
}