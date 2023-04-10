package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.*;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
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

    public Either<FailureResponse, CreateWorksheetResponse> createWorksheet(CreateWorksheetRequest dto) {
        String extractionServiceUrl = "http://localhost:8300/api/v1/metadata";
        String restorationServiceUrl = "http://localhost:8200/api/v1/restorations";
        String blueprintServiceUrl = "http://localhost:8100/api/v1/blueprints";

        WebClient webClient = webClientBuilder.build();

        Mono<Blueprint> blueprintResponse = getResponseWithResilience(webClient, blueprintServiceUrl, dto, Blueprint.class);
        Mono<Restoration> restorationResponse = getResponseWithResilience(webClient, restorationServiceUrl, dto, Restoration.class);
        Mono<Metadata> metadataResponse = getResponseWithResilience(webClient, extractionServiceUrl, dto, Metadata.class);

        CreateWorksheetResponse response = Mono.zip(blueprintResponse, restorationResponse, metadataResponse)
                .flatMap(tuple -> {
                    Blueprint blueprint = tuple.getT1();
                    Restoration restoration = tuple.getT2();
                    Metadata metadata = tuple.getT3();
                    return Mono.just(new CreateWorksheetResponse(blueprint, restoration, metadata));
                })
                .block();

        log.info("Is this called??");
        log.info("Is this called??");
        log.info("Is this called??");
        log.info("Is this called??");
        log.info("Is this called??");
        log.info("Is this called??");

        return response;
    }

    private <T> Mono<T> getResponseWithResilience(WebClient webClient, String url, CreateWorksheetRequest dto, Class<T> responseType) {
        // Configure Retry policy
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .build();
        Retry retryPolicy = Retry.of("worksheetServiceRetry", retryConfig);

        // Configure TimeLimiter
        TimeLimiter timeLimiter = TimeLimiter.of(Duration.ofSeconds(2));

        // Configure CircuitBreaker
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("worksheetServiceCircuitBreaker");

        return webClient.get()
                .uri(UriComponentsBuilder.fromHttpUrl(url).queryParam("blueprint_id", dto.blueprintId()).toUriString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(responseBody -> {
                            String errorMessage = String.format("%s Error occurred when calling %s: %s", clientResponse.statusCode(), url, responseBody);
                            log.error(errorMessage);
                            return Mono.error(new RuntimeException(errorMessage));
                        })
                )
                .bodyToMono(responseType)
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
