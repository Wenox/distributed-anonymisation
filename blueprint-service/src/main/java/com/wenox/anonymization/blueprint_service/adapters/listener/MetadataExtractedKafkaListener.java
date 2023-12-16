package com.wenox.anonymization.blueprint_service.adapters.listener;

import com.wenox.anonymization.blueprint_service.domain.ports.MetadataExtractedListener;
import com.wenox.anonymization.blueprint_service.domain.service.BlueprintSagaService;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetadataExtractedKafkaListener implements MetadataExtractedListener {

    private final BlueprintSagaService blueprintSagaService;

    @KafkaListener(topics = KafkaConstants.TOPIC_METADATA_SUCCESS, groupId = "blueprint-service-group")
    @Override
    public void onExtractSuccess(MetadataExtractedSuccessEvent event) {
        log.info("Received {}", event);
        blueprintSagaService.handleExtractionSuccess(event);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_METADATA_FAILURE, groupId = "blueprint-service-group")
    @Override
    public void onExtractFailure(MetadataExtractedFailureEvent event) {
        log.info("Received {}", event);
        blueprintSagaService.handleExtractionFailure(event);
    }
}
