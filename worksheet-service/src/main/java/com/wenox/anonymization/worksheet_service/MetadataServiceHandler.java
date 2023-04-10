package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MetadataServiceHandler extends AbstractServiceHandler<Metadata> {

    public MetadataServiceHandler(WebClient.Builder webClientBuilder) {
        super(webClientBuilder);
    }

    @Override
    protected String getServiceUrl() {
        return "http://localhost:8300/api/v1/metadata";
    }

    @Override
    protected Class<Metadata> getResponseType() {
        return Metadata.class;
    }
}
