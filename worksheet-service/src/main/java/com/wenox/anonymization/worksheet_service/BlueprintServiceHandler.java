package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Blueprint;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BlueprintServiceHandler extends AbstractServiceHandler<Blueprint> {

    public BlueprintServiceHandler(WebClient.Builder webClientBuilder) {
        super(webClientBuilder);
    }

    @Override
    protected String getServiceUrl() {
        // todo:
        return "http://localhost:8100/api/v1/blueprints";
    }

    @Override
    protected Class<Blueprint> getResponseType() {
        return Blueprint.class;
    }
}