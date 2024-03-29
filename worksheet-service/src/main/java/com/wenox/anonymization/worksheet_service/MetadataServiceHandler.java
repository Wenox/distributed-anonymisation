package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Metadata;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MetadataServiceHandler extends AbstractServiceHandler<Metadata> {

    public MetadataServiceHandler(WebClient.Builder webClientBuilder, Retry retry, CircuitBreaker circuitBreaker) {
        super(webClientBuilder, retry, circuitBreaker);
    }

    @Override
    protected String getServiceUrl() {
        // todo:
        // Consul or Kubernetes
        return "http://metadata-extraction-service:8300/api/v1/metadata";
    }

    @Override
    protected Class<Metadata> getResponseType() {
        return Metadata.class;
    }
}
