package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseRestoreListener implements RestoreListener {

    private final BlueprintRepository blueprintRepository;

    @Override
    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_SUCCESS, groupId = "blueprint-service-group")
    public void onRestoreSuccess(DatabaseRestoredSuccessEvent event) {
        log.info("Received {}", event);
        updateBlueprintStatus(event.getBlueprintId(), BlueprintStatus.DUMP_STORE_SUCCESS);
    }

    @Override
    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_FAILURE, groupId = "blueprint-service-group")
    public void onRestoreFailure(DatabaseRestoredFailureEvent event) {
        log.info("Received {}", event);
        updateBlueprintStatus(event.getBlueprintId(), BlueprintStatus.DUMP_STORE_FAILURE);
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
