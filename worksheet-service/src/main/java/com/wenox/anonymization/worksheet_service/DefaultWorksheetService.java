package com.wenox.anonymization.worksheet_service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultWorksheetService {

    private final WorksheetRepository worksheetRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public CreateWorksheetResponse createWorksheet(CreateWorksheetRequest dto) {
        String extractionServiceUrl = "http://localhost:8080/api/v1/metadata/{id}";
        String blueprintServiceUrl = "http://localhost:8081/api/v1/blueprint/{id}";

        // Configure Retry policy
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .build();

        // Configure Retry policy
        Retry retryPolicy = Retry.of("worksheetServiceRetry", retryConfig);

        // Configure TimeLimiter
        TimeLimiter timeLimiter = TimeLimiter.of(Duration.ofSeconds(2));

        // Configure CircuitBreaker
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("worksheetServiceCircuitBreaker");

        // Make parallel calls to extraction-service and blueprint-service with resilience patterns
        Mono<MetadataResponse> metadataResponseMono = webClientBuilder.build()
                .get()
                .uri(extractionServiceUrl, 1)
                .retrieve()
                .bodyToMono(MetadataResponse.class)
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));


        return null;
    }
}
