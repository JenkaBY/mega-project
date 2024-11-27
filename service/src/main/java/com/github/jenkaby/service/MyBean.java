package com.github.jenkaby.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class MyBean implements BeanNameAware, ApplicationContextAware,
        InitializingBean, DisposableBean {

    private String message;

    public void sendMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("[lifecycle] " + BeanNameAware.class.getSimpleName() + "setBeanName executed ---");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        System.out.println("[lifecycle] " + ApplicationContextAware.class.getSimpleName() + ".setApplicationContext executed ---");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("[lifecycle] @PostConstruct --- @PostConstruct executed ---");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("[lifecycle] " + InitializingBean.class.getSimpleName() + ".afterPropertiesSet executed ---");
    }

    public void initMethod() {
        System.out.println("[lifecycle] @Bean(initMethod = \"initMethod\") initMethod executed ---");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("[lifecycle] @PreDestroy executed ---");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("[lifecycle] " + DisposableBean.class.getSimpleName() + ".destroy executed ---");
    }

    public void destroyMethod() {
        System.out.println("[lifecycle] @Bean(destroyMethod = \"destroyMethod\") destroyMethod executed ---");
    }

}
