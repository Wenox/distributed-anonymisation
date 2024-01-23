package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Blueprint;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BlueprintServiceHandler extends AbstractServiceHandler<Blueprint> {

    public BlueprintServiceHandler(WebClient.Builder webClientBuilder, Retry retry, CircuitBreaker circuitBreaker) {
        super(webClientBuilder, retry, circuitBreaker);
    }

    @Override
    protected String getServiceUrl() {
        // todo:
        return "http://blueprint-service:8100/api/v1/blueprints";
    }

    @Override
    protected Class<Blueprint> getResponseType() {
        return Blueprint.class;
    }
}