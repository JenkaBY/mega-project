package com.github.jenkaby.config.web;

import com.github.jenkaby.config.security.support.LoggedUserResolver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private final List<String> allowedOrigins;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoggedUserResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .combine(corsConfiguration());
    }

    @Bean("customCorsConfiguration")
    CorsConfigurationSource corsConfigurationSource() {
        var configuration = corsConfiguration();

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    CorsConfiguration corsConfiguration() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);

        configuration.setAllowedMethods(List.of("GET", "PUT", "DELETE", "OPTIONS"));

        var all = List.of(CorsConfiguration.ALL);
        configuration.setAllowedHeaders(all);
        configuration.setExposedHeaders(all);

        configuration.setMaxAge(Duration.ofSeconds(3600L));
        return configuration;
    }
}
