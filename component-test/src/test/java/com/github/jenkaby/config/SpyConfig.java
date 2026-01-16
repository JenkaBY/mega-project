package com.github.jenkaby.config;

import com.github.jenkaby.service.TransactionJsonListenerService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
public class SpyConfig {

    @Bean
    @Primary
    TransactionJsonListenerService transactionJsonListenerServiceSpy(TransactionJsonListenerService transactionJsonListenerService) {
        return Mockito.spy(transactionJsonListenerService);
    }
}
