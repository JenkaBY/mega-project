package com.github.jenkaby.config.telemetry;


import com.github.jenkaby.service.delay.ClientDelayService;
import com.github.jenkaby.service.support.MeasurementService;
import com.github.jenkaby.service.support.MetricRecordService;
import com.github.jenkaby.service.support.ProxyType;
import com.github.jenkaby.service.support.annotation.RecordMetric;
import io.micrometer.core.instrument.Tag;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


@Slf4j
@RequiredArgsConstructor
public class RecordLatencyBeanPostProcessor implements BeanPostProcessor {

    private static final Class<RecordMetric> TARGET_ANNOTATION = RecordMetric.class;

    private final ObjectProvider<MeasurementService> measurementServiceProvider;
    private final ObjectProvider<MetricRecordService> metricRecordServiceProvider;
    private final ProxyType proxyType;

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
                .filter(a -> a.proxyType() == proxyType)
                .orElse(null);
        if (annotation == null) {
            log.debug("Skipping {} because of missing {} annotation", beanName, TARGET_ANNOTATION.getSimpleName());
            return bean;
        }
        ProxyType.fromClass(bean.getClass());
        return new LatencyRecordingProxyFactory(bean, measurementServiceProvider, metricRecordServiceProvider)
                .getProxy(annotation.proxyType());
    }

    private Object getTargetObject(Object proxy) throws BeansException {
//         TODO improve it. We need to handle all types of proxies
        if (proxy instanceof Advised advised) {
            log.info("Proxy {} is instance of {}", proxy.getClass(), Advised.class);
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
            log.trace("[BPP interceptor] {}", invocation);
            var invokationMethod = invocation.getMethod();
            var declaredMethod = target.getClass().getDeclaredMethod(invokationMethod.getName(), invokationMethod.getParameterTypes());
            var latencyAnnotation = AnnotationUtils.getAnnotation(declaredMethod, TARGET_ANNOTATION);
            if (latencyAnnotation == null) {
                return invocation.proceed();
            }
            String proxyType = latencyAnnotation.proxyType().name();
            log.info("[BPP-{} interceptor] Track latency for '{}'", proxyType, invokationMethod.getName());
            var measured = getMeasurementService().measure(invocation::proceed);
            log.info("[BPP-{} interceptor] Measured latency for '{}' is {} ns", proxyType, invokationMethod.getName(), measured.getNanos());

            // Method Information and send metric
            String metricName = latencyAnnotation.metric().getMetricName();
            Tag[] tags = Arrays.stream(latencyAnnotation.tags())
                    .map(TelemetryTag::getTags)
                    .flatMap(Arrays::stream)
                    .toArray(Tag[]::new);
            getMetricRecordService().recordLatency(metricName, tags, Duration.of(measured.getNanos(), ChronoUnit.NANOS));
            return measured.value();
        }

        private Object getProxy(ProxyType proxyType) {
            proxyFactory.addAdvice(this);
            if (proxyType == ProxyType.CGLIB) {
                proxyFactory.setProxyTargetClass(true);
            }
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
}
