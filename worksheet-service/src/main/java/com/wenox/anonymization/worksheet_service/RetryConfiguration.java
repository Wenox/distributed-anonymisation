package com.wenox.anonymization.worksheet_service;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetryConfiguration {

    @Bean
    public RetryConfig customRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .build();
    }

    @Bean
    public Retry retryPolicy() {
        return Retry.of("worksheetServiceRetry", customRetryConfig());
    }
}
