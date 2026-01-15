package com.github.jenkaby.config;

import com.github.jenkaby.service.TransactionJsonListenerService;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Configuration
public class SpyConfig {

    @MockitoSpyBean(name = "transactionJsonListenerServiceSpy")
    private TransactionJsonListenerService transactionJsonListenerService;

}
