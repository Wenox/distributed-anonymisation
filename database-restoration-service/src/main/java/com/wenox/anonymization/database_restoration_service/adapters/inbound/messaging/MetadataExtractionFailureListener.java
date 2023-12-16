package com.wenox.anonymization.database_restoration_service.adapters.inbound.messaging;

import com.wenox.anonymization.database_restoration_service.domain.service.messaging.MetadataExtractionFailureService;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
class MetadataExtractionFailureListener {

    private final MetadataExtractionFailureService metadataExtractionFailureService;

    @KafkaListener(topics = KafkaConstants.TOPIC_METADATA_FAILURE, groupId = "blueprint-service-group")
    @Retryable(retryFor = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    void onExtractionFailure(MetadataExtractedFailureEvent event) {
        log.info("-----> Received compensating transaction {}", event);
        metadataExtractionFailureService.handle(event);
        log.info("<----- Finished compensating transaction {}", event);
    }
}
