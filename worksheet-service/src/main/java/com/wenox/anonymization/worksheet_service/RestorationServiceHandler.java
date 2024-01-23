package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Restoration;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RestorationServiceHandler extends AbstractServiceHandler<Restoration> {

    public RestorationServiceHandler(WebClient.Builder webClientBuilder, Retry retry, CircuitBreaker circuitBreaker) {
        super(webClientBuilder, retry, circuitBreaker);
    }

    @Override
    protected String getServiceUrl() {
        // todo:
        // Consul or Kubernetes
        return "http://database-restoration-service:8200/api/v1/restorations";
    }

    @Override
    protected Class<Restoration> getResponseType() {
        return Restoration.class;
    }
}

