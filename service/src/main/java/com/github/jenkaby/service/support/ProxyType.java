package com.github.jenkaby.service.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Proxy;


@Slf4j
public enum ProxyType {
    JDK_DYNAMIC,
    CGLIB,
    NONE;

    public static ProxyType fromClass(Class<?> clazz) {
        log.debug("Trying to define a proxy type of {}", clazz);
        if (Proxy.isProxyClass(clazz)) {
            log.debug("Proxy {} is JDK Dynamic Proxy", clazz);
            return ProxyType.JDK_DYNAMIC;
        } else if (clazz.getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
            log.debug("Proxy {} is CGLIB Proxy", clazz);
            return ProxyType.CGLIB;
        }
        log.debug("{} is not a proxy", clazz);
        return ProxyType.NONE;
    }
}
