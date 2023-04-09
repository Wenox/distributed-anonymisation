package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Blueprint;
import com.wenox.anonymization.worksheet_service.domain.CreateWorksheetResponse;
import com.wenox.anonymization.worksheet_service.domain.Metadata;
import com.wenox.anonymization.worksheet_service.domain.Restoration;
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
import org.springframework.web.util.UriComponentsBuilder;
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
        String extractionServiceUrl = "http://localhost:8300/api/v1/metadata";
        String restorationServiceUrl = "http://localhost:8200/api/v1/restorations";
        String blueprintServiceUrl = "http://localhost:8100/api/v1/blueprints";

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

        Mono<Blueprint> blueprintResponse = webClientBuilder.build()
                .get()
                .uri(UriComponentsBuilder.fromHttpUrl(blueprintServiceUrl).queryParam("blueprint_id", dto.blueprintId()).toUriString())
                .retrieve()
                .bodyToMono(Blueprint.class)
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));

        Mono<Restoration> restorationResponse = webClientBuilder.build()
                .get()
                .uri(UriComponentsBuilder.fromHttpUrl(restorationServiceUrl).queryParam("blueprint_id", dto.blueprintId()).toUriString())
                .retrieve()
                .bodyToMono(Restoration.class)
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));

        Mono<Metadata> metadataResponseMono = webClientBuilder.build()
                .get()
                .uri(UriComponentsBuilder.fromHttpUrl(extractionServiceUrl).queryParam("blueprint_id", dto.blueprintId()).toUriString())
                .retrieve()
                .bodyToMono(Metadata.class)
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));


        return Mono.zip(blueprintResponse, restorationResponse, metadataResponseMono)
                .flatMap(tuple -> {
                    Blueprint blueprint = tuple.getT1();
                    Restoration restoration = tuple.getT2();
                    Metadata metadata = tuple.getT3();

                    return Mono.just(new CreateWorksheetResponse(blueprint, restoration, metadata));
                })
                .block();
    }
}
