package com.anonymization.etl.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerRegistryConfig {

    @Value("${streaming-etl.circuit-breaker.failure-rate-threshold:50}")
    private int failureRateThreshold;

    @Value("${streaming-etl.circuit-breaker.wait-duration-in-open-state:30}")
    private int waitDurationInOpenState;

    @Value("${streaming-etl.circuit-breaker.sliding-window-size:10}")
    private int slidingWindowSize;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .slidingWindowSize(slidingWindowSize)
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();

        circuitBreakerRegistry.addConfiguration("etlStreamingCircuitBreaker", circuitBreakerConfig);

        return circuitBreakerRegistry;
    }
}
