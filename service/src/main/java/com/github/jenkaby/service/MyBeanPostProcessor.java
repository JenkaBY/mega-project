package com.github.jenkaby.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof MyBean) {
            System.out.println("[lifecycle] BeanPostProcessor.postProcessBeforeInitialization ---");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof MyBean) {
            System.out.println("[lifecycle] BeanPostProcessor.postProcessAfterInitialization ---");
        }
        return bean;
    }

}
