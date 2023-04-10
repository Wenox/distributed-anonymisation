package com.wenox.anonymization.worksheet_service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
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

    public Mono<Either<ErrorInfo, T>> getResponse(CreateWorksheetRequest dto) {
        WebClient webClient = webClientBuilder.build();
        String url = getServiceUrl();

        return webClient.get()
                .uri(UriComponentsBuilder.fromHttpUrl(url).queryParam("blueprint_id", dto.blueprintId()).toUriString())
                .retrieve()
                .bodyToMono(getResponseType())
                .timeout(Duration.ofSeconds(2))
                .map(Either::<ErrorInfo, T>right)
                .onErrorResume(this::handleError)
                .transformDeferred(this::applyResilience);
    }

    private Mono<Either<ErrorInfo, T>> handleError(Throwable throwable) {

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setDescription("Error occurred when calling " + getServiceUrl());
        errorInfo.setReason(throwable.getMessage());
        errorInfo.setException(throwable.getClass().getSimpleName());

        if (throwable instanceof WebClientResponseException exception) {
            errorInfo.setReason(exception.getResponseBodyAsString());
            errorInfo.setStatus(exception.getStatusCode().value());
            errorInfo.setPhrase(HttpStatus.resolve(exception.getStatusCode().value()).getReasonPhrase());
        }
        log.error("error info: {}", errorInfo);
        return Mono.just(Either.left(errorInfo));
    }

    private Publisher<Either<ErrorInfo, T>> applyResilience(Publisher<Either<ErrorInfo, T>> publisher) {
        RetryConfig retryConfig = createRetryConfig();
        Retry retryPolicy = Retry.of("worksheetServiceRetry", retryConfig);
        CircuitBreaker circuitBreaker = createCircuitBreaker();

        return Flux.from(publisher)
                .transformDeferred(RetryOperator.of(retryPolicy))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    private RetryConfig createRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .build();
    }

    private CircuitBreaker createCircuitBreaker() {
        return CircuitBreaker.ofDefaults("worksheetServiceCircuitBreaker");
    }
}