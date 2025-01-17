package com.github.jenkaby.config;

import com.github.jenkaby.service.TransactionJsonListenerService;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpyConfig {

    @SpyBean
    private TransactionJsonListenerService transactionJsonListenerService;

}
