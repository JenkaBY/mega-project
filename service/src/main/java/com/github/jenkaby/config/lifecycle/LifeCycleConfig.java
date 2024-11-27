package com.github.jenkaby.config.lifecycle;

import com.github.jenkaby.service.MyBean;
import com.github.jenkaby.service.MyBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LifeCycleConfig {

//    @Bean(initMethod = "initMethod", destroyMethod = "destroyMethod")
//    public MyBean myBean() {
//        return new MyBean();
//    }

//    @Bean
    public MyBeanFactoryBean myBeanFactoryBean() {
        return new MyBeanFactoryBean();
    }

    @Bean
    public MyBeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
