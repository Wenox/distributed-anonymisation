package com.anonymization.etl.core;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.Serializable;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class WebClientSupplier implements Supplier<WebClient>, Serializable {

    private final BroadcastSettings config;

    @Override
    public WebClient get() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(clientCodecConfigurer -> clientCodecConfigurer
                                .defaultCodecs()
                                .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .baseUrl(config.getRestorationServiceUrl())
                .build();
    }
}
