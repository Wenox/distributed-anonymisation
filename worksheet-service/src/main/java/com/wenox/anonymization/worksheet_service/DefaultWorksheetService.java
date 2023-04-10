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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

        Mono<Either<String, Blueprint>> blueprintResponse = getResponseWithResilience(webClient, blueprintServiceUrl, dto, Blueprint.class);
        Mono<Either<String, Restoration>> restorationResponse = getResponseWithResilience(webClient, restorationServiceUrl, dto, Restoration.class);
        Mono<Either<String, Metadata>> metadataResponse = getResponseWithResilience(webClient, extractionServiceUrl, dto, Metadata.class);

        Tuple3<Either<String, Blueprint>, Either<String, Restoration>, Either<String, Metadata>> responseTuple =
                Mono.zip(blueprintResponse, restorationResponse, metadataResponse).block();

        List<String> errors = new ArrayList<>();
        responseTuple.getT1().peekLeft(errors::add);
        responseTuple.getT2().peekLeft(errors::add);
        responseTuple.getT3().peekLeft(errors::add);

        if (!errors.isEmpty()) {
            return Either.left(new FailureResponse(errors));
        }

        CreateWorksheetResponse response = new CreateWorksheetResponse(
                responseTuple.getT1().get(),
                responseTuple.getT2().get(),
                responseTuple.getT3().get()
        );
        return Either.right(response);
    }

    private <T> Mono<Either<String, T>> getResponseWithResilience(WebClient webClient, String url, CreateWorksheetRequest dto, Class<T> responseType) {
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
                .bodyToMono(responseType)
                .map(Either::<String, T>right)
                .onErrorResume(throwable -> {
                    String errorMessage;
                    if (throwable instanceof WebClientResponseException exception) {
                        String responseBody = exception.getResponseBodyAsString();
                        errorMessage = String.format("%s Error occurred when calling %s: %s", exception.getStatusCode(), url, responseBody);
                    } else {
                        errorMessage = "Error occurred when calling " + url + ": " + throwable.getMessage();
                    }
                    log.error(errorMessage);
                    return Mono.just(Either.left(errorMessage));
                })
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

}
