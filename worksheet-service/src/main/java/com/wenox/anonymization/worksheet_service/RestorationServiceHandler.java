package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Restoration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RestorationServiceHandler extends AbstractServiceHandler<Restoration> {

    public RestorationServiceHandler(WebClient.Builder webClientBuilder) {
        super(webClientBuilder);
    }

    @Override
    protected String getServiceUrl() {
        // todo:
        // Consul or Kubernetes
        return "http://localhost:8200/api/v1/restorations";
    }

    @Override
    protected Class<Restoration> getResponseType() {
        return Restoration.class;
    }
}

