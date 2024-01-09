package com.anonymization.etl.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
public class WebClientSink implements Serializable {

    private final Supplier<WebClient> webClientSupplier;
    private final AtomicReference<WebClient> webClientRef;

    public WebClientSink(Supplier<WebClient> webClientSupplier) {
        this.webClientSupplier = webClientSupplier;
        this.webClientRef = new AtomicReference<>();
    }

    public WebClient getWebClient() {
        return webClientRef.updateAndGet(currentWebClient -> {
            if (currentWebClient == null) {
                log.info("Preparing for WebClient instantiation...");
                return webClientSupplier.get();
            }
            return currentWebClient;
        });
    }

    public static WebClientSink apply(BroadcastSettings config) {
        return new WebClientSink(new WebClientSupplier(config));
    }

    public static ClassTag<WebClientSink> getClassTag() {
        return ClassTag$.MODULE$.apply(WebClientSink.class);
    }

}
