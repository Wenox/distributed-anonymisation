package com.anonymization.etl.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

import java.io.Serializable;
import java.util.function.Supplier;

@Slf4j
public class WebClientSink implements Serializable {

    private final Supplier<WebClient> webClientSupplier;
    private transient WebClient webClient;

    public WebClientSink(Supplier<WebClient> webClientSupplier) {
        this.webClientSupplier = webClientSupplier;
    }

    public WebClient getWebClient() {
        if (webClient == null) {
            log.info("Preparing for WebClient instantiation...");
            webClient = webClientSupplier.get();
        }
        return webClient;
    }

    public static WebClientSink apply(BroadcastSettings config) {
        return new WebClientSink(new WebClientSupplier(config));
    }

    public static ClassTag<WebClientSink> getClassTag() {
        return ClassTag$.MODULE$.apply(WebClientSink.class);
    }

}
