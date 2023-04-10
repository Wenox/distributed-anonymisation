package com.wenox.anonymization.worksheet_service;

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
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractServiceHandler<T> {

    protected final WebClient.Builder webClientBuilder;

    protected abstract String getServiceUrl();
    protected abstract Class<T> getResponseType();

    public Mono<Either<String, T>> getResponse(CreateWorksheetRequest dto) {
        WebClient webClient = webClientBuilder.build();
        String url = getServiceUrl();

        return webClient.get()
                .uri(UriComponentsBuilder.fromHttpUrl(url).queryParam("blueprint_id", dto.blueprintId()).toUriString())
                .retrieve()
                .bodyToMono(getResponseType())
                .map(Either::<String, T>right)
                .onErrorResume(this::handleError)
                .transformDeferred(this::applyResilience);
    }

    private Mono<Either<String, T>> handleError(Throwable throwable) {
        String errorMessage;
        if (throwable instanceof WebClientResponseException exception) {
            String responseBody = exception.getResponseBodyAsString();
            errorMessage = String.format("%s Error occurred when calling %s: %s", exception.getStatusCode(), getServiceUrl(), responseBody);
        } else {
            errorMessage = "Error occurred when calling " + getServiceUrl() + ": " + throwable.getMessage();
        }
        log.error(errorMessage);
        return Mono.just(Either.left(errorMessage));
    }

    private Publisher<Either<String, T>> applyResilience(Publisher<Either<String, T>> publisher) {
        RetryConfig retryConfig = createRetryConfig();
        Retry retryPolicy = Retry.of("worksheetServiceRetry", retryConfig);
        TimeLimiter timeLimiter = createTimeLimiter();
        CircuitBreaker circuitBreaker = createCircuitBreaker();

        return Flux.from(publisher)
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    private RetryConfig createRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .build();
    }

    private TimeLimiter createTimeLimiter() {
        return TimeLimiter.of(Duration.ofSeconds(2));
    }

    private CircuitBreaker createCircuitBreaker() {
        return CircuitBreaker.ofDefaults("worksheetServiceCircuitBreaker");
    }
}