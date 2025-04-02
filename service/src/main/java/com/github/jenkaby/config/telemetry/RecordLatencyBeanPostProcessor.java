package com.github.jenkaby.config.telemetry;


import com.github.jenkaby.service.delay.ClientDelayService;
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


@Component
@Slf4j
@RequiredArgsConstructor
public class RecordLatencyBeanPostProcessor implements BeanPostProcessor {

    private static final Class<RecordMetric> TARGET_ANNOTATION = RecordMetric.class;

    private final ObjectProvider<MeasurementService> measurementServiceProvider;
    private final ObjectProvider<MetricRecordService> metricRecordServiceProvider;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof ClientDelayService)) {
            return bean;
        }
        var target = this.getTargetObject(bean);

        var annotation = Arrays.stream(target.getClass().getMethods())
                .map(m -> Optional.ofNullable(AnnotationUtils.getAnnotation(m, TARGET_ANNOTATION)))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity())
                .orElse(null);
        if (annotation == null) {
            log.debug("Skipping {} because of missing {} annotation", beanName, TARGET_ANNOTATION.getSimpleName());
            return bean;
        }
        checkProxyType(bean);
        return new LatencyRecordingProxyFactory(bean, measurementServiceProvider, metricRecordServiceProvider)
                .getProxy();
//        return addLatencyMeasurementInterceptor(bean);
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
            log.info("Proxy {} is instance of {}", proxy, Advised.class);
            try {
                return advised.getTargetSource().getTarget();
            } catch (Exception e) {
                throw new FatalBeanException("Error getting target of JDK proxy", e);
            }
        }
        return proxy;
    }

    static class LatencyRecordingProxyFactory implements org.aopalliance.intercept.MethodInterceptor {

        private final Object measurementServiceMutex = new Object();
        private final Object metricRecordServiceMutex = new Object();
        private MeasurementService measurementService;
        private MetricRecordService metricRecordService;

        private final Object target;
        private final ProxyFactory proxyFactory;
        private final ObjectProvider<MeasurementService> _measurementServiceProvider;
        private final ObjectProvider<MetricRecordService> _metricRecordServiceProvider;

        LatencyRecordingProxyFactory(Object target, ObjectProvider<MeasurementService> measurementServiceProvider, ObjectProvider<MetricRecordService> metricRecordServiceProvider) {
            this.proxyFactory = new ProxyFactory(target);
            this.target = target;
            _measurementServiceProvider = measurementServiceProvider;
            _metricRecordServiceProvider = metricRecordServiceProvider;
        }

        @Nullable
        @Override
        public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
            log.trace("[BPP-? interceptor] {}", invocation);
            var invokationMethod = invocation.getMethod();
            var declaredMethod = target.getClass().getDeclaredMethod(invokationMethod.getName(), invokationMethod.getParameterTypes());
            var latencyAnnotation = AnnotationUtils.getAnnotation(declaredMethod, TARGET_ANNOTATION);
            if (latencyAnnotation == null) {
                return invocation.proceed();
            }
            log.info("[BPP-? interceptor] Track latency for '{}'", invokationMethod.getName());
            var measured = getMeasurementService().measure(invocation::proceed);
            log.info("[BPP-? interceptor] Measured latency for '{}' is {} ns", invokationMethod.getName(), measured.getNanos());

            // Method Information and send metric
            String metricName = latencyAnnotation.metric().getMetricName();
            Tag[] tags = Arrays.stream(latencyAnnotation.tags())
                    .map(TelemetryTag::getTags)
                    .flatMap(Arrays::stream)
                    .toArray(Tag[]::new);
            getMetricRecordService().recordLatency(metricName, tags, Duration.of(measured.getNanos(), ChronoUnit.NANOS));
            return measured.value();
        }

        Object getProxy() {
            proxyFactory.addAdvice(this);
            return proxyFactory.getProxy();
        }

        private MetricRecordService getMetricRecordService() {
            if (metricRecordService != null) {
                return metricRecordService;
            } else {
                synchronized (metricRecordServiceMutex) {
                    if (metricRecordService != null) {
                        return metricRecordService;
                    }
                    metricRecordService = _metricRecordServiceProvider.getObject();
                }
            }
            return metricRecordService;
        }

        private MeasurementService getMeasurementService() {
            if (measurementService != null) {
                return measurementService;
            } else {
                synchronized (measurementServiceMutex) {
                    if (measurementService != null) {
                        return measurementService;
                    }
                    measurementService = _measurementServiceProvider.getObject();
                }
            }
            return measurementService;
        }
    }

//    private Object addLatencyMeasurementInterceptorAop(Object bean) {
//        log.info("Proxying the {} class by ProxyFactory", bean.getClass());
//
//        ProxyFactory proxyFactory = new ProxyFactory(bean);
//        proxyFactory.addAdvice(new org.aopalliance.intercept.MethodInterceptor() {
//
//
//            @Nullable
//            @Override
//            public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
//
//                log.trace("[BPP-? interceptor] {}", invocation);
//                var invokationMethod = invocation.getMethod();
//                var declaredMethod = bean.getClass().getDeclaredMethod(invokationMethod.getName(), invokationMethod.getParameterTypes());
//                var latencyAnnotation = AnnotationUtils.getAnnotation(declaredMethod, TARGET_ANNOTATION);
//                if (latencyAnnotation == null) {
//                    return invocation.proceed();
//                }
//                log.info("[BPP-? interceptor] Track latency for {}", invokationMethod.getName());
//                var measured = getMeasurementService().measure(invocation::proceed);
//                log.info("[BPP-? interceptor] Measured latency for {} is {} ns", invokationMethod.getName(), measured.getNanos());
//
//                // Method Information and send metric
//                String metricName = latencyAnnotation.metric().getMetricName();
//                Tag[] tags = Arrays.stream(latencyAnnotation.tags())
//                        .map(TelemetryTag::getTags)
//                        .flatMap(Arrays::stream)
//                        .toArray(Tag[]::new);
//                getMetricRecordService().recordLatency(metricName, tags, Duration.of(measured.getNanos(), ChronoUnit.NANOS));
//                return measured.value();
//            }
//
//            public MetricRecordService getMetricRecordService() {
//                if (metricRecordService != null) {
//                    return metricRecordService;
//                } else {
//                    synchronized (metricRecordServiceMutex) {
//                        if (metricRecordService != null) {
//                            return metricRecordService;
//                        }
//                        metricRecordService = metricRecordServiceProvider.getObject();
//                    }
//                }
//                return metricRecordService;
//            }
//
//            public MeasurementService getMeasurementService() {
//                if (measurementService != null) {
//                    return measurementService;
//                } else {
//                    synchronized (measurementServiceMutex) {
//                        if (measurementService != null) {
//                            return measurementService;
//                        }
//                        measurementService = measurementServiceProvider.getObject();
//                    }
//                }
//                return measurementService;
//            }
//        });
//
//        Object proxy = proxyFactory.getProxy();
//        log.info("Bean {} is proxied at {}", bean.getClass(), proxy.getClass());
//        return proxy;
//    }

//    private Object addLatencyMeasurementInterceptor(Object bean) {
//        MeasurementService measurementService = measurementServiceProvider.getObject();
//        MetricRecordService metricRecordService = metricRecordServiceProvider.getObject();
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(bean.getClass());
//        enhancer.setInterfaces(bean.getClass().getInterfaces());
//// FIXME is not working
//        enhancer.setCallback(new MethodInterceptor() {
//            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
////                log.trace("[BPP-CGLib] interceptor {}", proxy);
//                if (AnnotationUtils.getAnnotation(method, TARGET_ANNOTATION) == null) {
//                    return proxy.invoke(bean, args);
//                }
//                log.debug("[BPP-CGLib] Track latency for '{}'", method.getName());
//                var measured = measurementService.measure(() -> proxy.invokeSuper(bean, args));
//                log.debug("[BPP-CGLib] Measured latency for '{}' is {} ns", method.getName(), measured.getNanos());
//
//                // Method Information and send metric
//
//                var latencyAnnotation = method.getAnnotation(TARGET_ANNOTATION);
//                String metricName = latencyAnnotation.metric().getMetricName();
//                Tag[] tags = Arrays.stream(latencyAnnotation.tags())
//                        .map(TelemetryTag::getTags)
//                        .flatMap(Arrays::stream)
//                        .toArray(Tag[]::new);
//                metricRecordService.recordLatency(metricName, tags, Duration.of(measured.getNanos(), ChronoUnit.NANOS));
//                return measured.value();
//            }
//        });
//        return enhancer.create();
//    }
}
