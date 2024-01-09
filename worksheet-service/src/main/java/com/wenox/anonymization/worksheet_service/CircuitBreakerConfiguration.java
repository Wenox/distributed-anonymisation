package com.wenox.anonymization.worksheet_service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {

    @Value("${external-services.circuit-breaker.failure-rate-threshold:50}")
    private int failureRateThreshold;

    @Value("${external-services.circuit-breaker.wait-duration-in-open-state:30}")
    private int waitDurationInOpenState;

    @Value("${external-services.circuit-breaker.sliding-window-size:10}")
    private int slidingWindowSize;

    @Bean
    public CircuitBreaker customCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .slidingWindowSize(slidingWindowSize)
                .build();

        return CircuitBreaker.of("worksheetServiceCircuitBreaker", circuitBreakerConfig);
    }
}