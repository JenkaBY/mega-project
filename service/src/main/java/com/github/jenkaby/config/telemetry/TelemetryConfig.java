package com.github.jenkaby.config.telemetry;

import com.github.jenkaby.service.support.BasicMetricRecordService;
import com.github.jenkaby.service.support.MeasurementService;
import com.github.jenkaby.service.support.MetricRecordService;
import com.github.jenkaby.service.support.ProxyType;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TelemetryConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public MetricRecordService metricRecordService(MeterRegistry registry) {
        return new BasicMetricRecordService(registry);
    }

    @Bean
    public BeanPostProcessor recordLatencyBeanPostProcessorDynamicProxy(ObjectProvider<MeasurementService> measurementServiceProvider,
                                                                        ObjectProvider<MetricRecordService> metricRecordServiceProvider) {
        return new RecordLatencyBeanPostProcessor(measurementServiceProvider, metricRecordServiceProvider, ProxyType.JDK_DYNAMIC);
    }

    @Bean
    public BeanPostProcessor recordLatencyBeanPostProcessorCGLibProxy(ObjectProvider<MeasurementService> measurementServiceProvider,
                                                                      ObjectProvider<MetricRecordService> metricRecordServiceProvider) {
        return new RecordLatencyBeanPostProcessor(measurementServiceProvider, metricRecordServiceProvider, ProxyType.CGLIB);
    }
}
