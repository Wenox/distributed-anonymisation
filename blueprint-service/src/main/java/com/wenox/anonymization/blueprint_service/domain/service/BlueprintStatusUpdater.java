package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.model.BlueprintStatus;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintMessagePublisher;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class BlueprintStatusUpdater {

    private final BlueprintRepository blueprintRepository;
    private final BlueprintMessagePublisher blueprintPublisher;

    void updateBlueprintStatusOnSuccess(Blueprint blueprint) {
        blueprint.setBlueprintStatus(BlueprintStatus.S3_STORE_SUCCESS);
        blueprint.setDumpStoreSuccess(true);
        blueprintRepository.save(blueprint);
        blueprintPublisher.sendBlueprintCreated(blueprint);
    }

    void updateBlueprintStatusOnFailure(Blueprint blueprint) {
        blueprint.setBlueprintStatus(BlueprintStatus.S3_STORE_FAILURE);
        blueprint.setDumpStoreSuccess(false);
        blueprintRepository.save(blueprint);
    }

    void updateBlueprintStatus(String blueprintId, BlueprintStatus status) {
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