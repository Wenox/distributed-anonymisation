package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Metadata;
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
        log.info("Creating worksheet. DTO : {}", dto);
        String extractionServiceUrl = "http://localhost:8300/api/v1/metadata/{id}";

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

        log.info("Calling {}", extractionServiceUrl);
        // Make parallel calls to extraction-service and blueprint-service with resilience patterns
        Metadata metadataResponseMono = webClientBuilder.build()
                .get()
                .uri(extractionServiceUrl, dto.blueprintId())
                .retrieve()
                .bodyToMono(Metadata.class)
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .block();

        try {
            log.info("Sleeping for 1s");
            Thread.sleep(1000);
        } catch (Exception ex) {}
        log.info("Successful call. Response metadata mono : {}", metadataResponseMono);
        log.info("Successful call. Response metadata : {}", metadataResponseMono);
        return null;
    }
}
