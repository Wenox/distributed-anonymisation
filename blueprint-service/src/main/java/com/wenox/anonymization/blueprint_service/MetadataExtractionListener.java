package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetadataExtractionListener implements ExtractListener {

    private final BlueprintRepository blueprintRepository;

    @KafkaListener(topics = KafkaConstants.TOPIC_METADATA_SUCCESS, groupId = "blueprint-service-group")
    @Override
    public void onExtractSuccess(MetadataExtractedSuccessEvent event) {
        log.info("Received {}", event);
        updateBlueprintStatus(event.getBlueprintId(), BlueprintStatus.METADATA_EXTRACTION_SUCCESS);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_METADATA_FAILURE, groupId = "blueprint-service-group")
    @Override
    public void onExtractFailure(MetadataExtractedFailureEvent event) {
        log.info("Received {}", event);
        updateBlueprintStatus(event.getBlueprintId(), BlueprintStatus.METADATA_EXTRACTION_FAILURE);
    }

    private void updateBlueprintStatus(String blueprintId, BlueprintStatus status) {
        try {
            blueprintRepository.findById(blueprintId)
                    .map(blueprint -> {
                        blueprint.setBlueprintStatus(status);
                        return blueprint;
                    })
                    .map(blueprintRepository::save)
                    .ifPresent(updatedBlueprint -> log.info("Updated status to {} of blueprint: {}", status, updatedBlueprint));
        } catch (Exception e) {
            log.error("Error updating status to {} for blueprintId: {} – error: {}", status, blueprintId, e.getMessage(), e);
        }
    }
}
