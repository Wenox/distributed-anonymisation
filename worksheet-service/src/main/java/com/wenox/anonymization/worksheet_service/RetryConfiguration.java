package com.wenox.anonymization.worksheet_service;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetryConfiguration {

    @Value("${external-services.retry-policy.max-attempts:3}")
    private int maxAttempts;

    @Value("${external-services.retry-policy.wait-duration:2000}")
    private int waitDuration;

    @Bean
    public RetryConfig customRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofMillis(waitDuration))
                .build();
    }

    @Bean
    public Retry retryPolicy() {
        return Retry.of("worksheetServiceRetry", customRetryConfig());
    }
}
