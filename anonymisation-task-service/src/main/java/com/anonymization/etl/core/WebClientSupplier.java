package com.anonymization.etl.core;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.Serializable;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class WebClientSupplier implements Supplier<WebClient>, Serializable {

    private final BroadcastSettings config;

    @Override
    public WebClient get() {
        return WebClient.create(config.getRestorationServiceUrl());
    }
}
