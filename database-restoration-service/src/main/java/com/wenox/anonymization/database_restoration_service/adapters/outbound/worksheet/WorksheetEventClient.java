package com.wenox.anonymization.database_restoration_service.adapters.outbound.worksheet;

import com.wenox.anonymization.database_restoration_service.domain.model.WorksheetProjection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class WorksheetEventClient {

    @Value("${transactional-outbox.worksheet-events.url}")
    private String url;

    @Value("${transactional-outbox.worksheet-events.fetch-timeout}")
    private Integer timeout;

    public Mono<List<WorksheetProjection>> fetchWorksheetEvents(LocalDateTime timestamp) {
        return WebClient.create()
                .get()
                .uri(UriComponentsBuilder.fromHttpUrl(url).queryParam("timestamp", timestamp.toString()).toUriString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<WorksheetProjection>>() {})
                .timeout(Duration.ofSeconds(timeout))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error fetching worksheet events: HTTP " + ex.getStatusCode());
                    return Mono.just(Collections.emptyList());
                });
    }
}
