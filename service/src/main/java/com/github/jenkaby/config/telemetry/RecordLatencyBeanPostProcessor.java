package com.github.jenkaby.config.telemetry;


import com.github.jenkaby.service.ClientDelayService;
import com.github.jenkaby.service.support.MeasurementService;
import com.github.jenkaby.service.support.MetricRecordService;
import com.github.jenkaby.service.support.annotation.RecordMetric;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;


@Component
@Slf4j
public class RecordLatencyBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof ClientDelayService)) {
            return bean;
        }
        Object target = this.getTargetObject(bean);

        var annotation = Arrays.stream(target.getClass().getMethods())
                .map(m -> Optional.ofNullable(AnnotationUtils.getAnnotation(m, RecordMetric.class)))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(o -> o)
                .orElse(null);
        if (annotation == null) {
            log.debug("Skipping {} because of missing {} annotation", beanName, RecordMetric.class.getSimpleName());
            return bean;
        }
        final MeasurementService measurementService = applicationContext.getBean(MeasurementService.class);
        final MetricRecordService metricRecordService = applicationContext.getBean(MetricRecordService.class);

        checkProxyType(bean);

        return addLatencyMeasurementInterceptorAop(bean, target.getClass(), measurementService, metricRecordService);
    }

    private void checkProxyType(Object bean) {
        Class<?> aClass = bean.getClass();
        if (AopUtils.isJdkDynamicProxy(bean)) {
            log.info("Proxy {} is JDK Dynamic Proxy", aClass);
        } else if (AopUtils.isCglibProxy(bean)) {
            log.info("Proxy {} is CGLIB Proxy", aClass);
        } else {
            log.info("{} is not a proxy", aClass);
        }
    }

    private Object getTargetObject(Object proxy) throws BeansException {
//         TODO improve it. We need to handle all types of proxies
        if (proxy instanceof Advised advised) {
            try {
                return advised.getTargetSource().getTarget();
            } catch (Exception e) {
                throw new FatalBeanException("Error getting target of JDK proxy", e);
            }
        }
        return proxy;
    }

    private <T> Object addLatencyMeasurementInterceptor(Object bean, Class<T> clazz, MeasurementService measurementService, MetricRecordService metricRecordService) {
        log.info("++++ TARGET CLASS " + clazz);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        MethodInterceptor methodInterceptor = new RecordLatencyInterceptor(measurementService, metricRecordService);
        enhancer.setCallback(methodInterceptor);
        return (T) enhancer.create();
    }

    private Object addLatencyMeasurementInterceptorAop(Object bean, Class<?> clazz, MeasurementService measurementService, MetricRecordService metricRecordService) {
        log.info("Proxying the {} class by ProxyFactory", clazz);
//        This works for CGLib proxied bean(For target class implementing interface)
        ProxyFactory proxyFactory = new ProxyFactory(bean);
        var methodInterceptor = new RecordLatencyAopInterceptor(measurementService, metricRecordService);
        proxyFactory.addAdvice(methodInterceptor);
        return proxyFactory.getProxy();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @RequiredArgsConstructor
    private static class RecordLatencyAopInterceptor implements org.aopalliance.intercept.MethodInterceptor {

        private final MeasurementService measurementService;
        private final MetricRecordService metricRecordService;

        @Nullable
        @Override
        public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
            log.info("[BPP-aop interceptor] {}", invocation);
            if (AnnotationUtils.getAnnotation(invocation.getMethod(), RecordMetric.class) == null) {
                return invocation.proceed();
            }
            log.debug("[BPP-aop interceptor] Track latency for {}", invocation.getMethod().getName());
            var measured = measurementService.measure(invocation::proceed);
            log.debug("[BPP-aop interceptor] Measured latency for {} is {} ns", invocation.getMethod().getName(), measured.getNanos());

            // Method Information and send metric

            var latencyAnnotation = invocation.getMethod().getAnnotation(RecordMetric.class);
            String metricName = latencyAnnotation.metric().getMetricName();
            Tag[] tags = Arrays.stream(latencyAnnotation.tags())
                    .map(TelemetryTag::getTags)
                    .flatMap(Arrays::stream)
                    .toArray(Tag[]::new);
            metricRecordService.recordLatency(metricName, tags, Duration.of(measured.getNanos(), ChronoUnit.NANOS));
            return measured.value();
        }
    }

    @RequiredArgsConstructor
    private static class RecordLatencyInterceptor implements MethodInterceptor {

        private final MeasurementService measurementService;
        private final MetricRecordService metricRecordService;

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (AnnotationUtils.getAnnotation(method, RecordMetric.class) == null) {
                return method.invoke(obj, args);
            }
            log.debug("[BPP-CGLib] Track latency for {}", proxy);
            var measured = measurementService.measure(() -> proxy.invokeSuper(obj, args));
            log.debug("[BPP-CGLib] Measured latency for {} is {} ns", proxy, measured.getNanos());

            // Method Information and send metric

            var latencyAnnotation = method.getAnnotation(RecordMetric.class);
            String metricName = latencyAnnotation.metric().getMetricName();
            Tag[] tags = Arrays.stream(latencyAnnotation.tags())
                    .map(TelemetryTag::getTags)
                    .flatMap(Arrays::stream)
                    .toArray(Tag[]::new);
            metricRecordService.recordLatency(metricName, tags, Duration.of(measured.getNanos(), ChronoUnit.NANOS));
            return measured.value();
        }
    }
}
