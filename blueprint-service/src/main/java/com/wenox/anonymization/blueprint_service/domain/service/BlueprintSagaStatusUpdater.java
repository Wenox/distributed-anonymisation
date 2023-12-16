package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.model.BlueprintSagaStatus;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class BlueprintSagaStatusUpdater {

    private final BlueprintRepository blueprintRepository;

    void updateSagaStatusOnDumpStoreSuccess(Blueprint blueprint) {
        blueprint.setBlueprintSagaStatus(BlueprintSagaStatus.DUMP_STORE_SUCCESS);
        blueprint.setDumpStoreSuccess(true);
        blueprintRepository.save(blueprint);
    }

    void updateSagaStatusOnDumpStoreFailure(Blueprint blueprint) {
        blueprint.setBlueprintSagaStatus(BlueprintSagaStatus.DUMP_STORE_FAILURE);
        blueprint.setDumpStoreSuccess(false);
        blueprintRepository.save(blueprint);
    }

    void updateBlueprintSagaStatus(String blueprintId, BlueprintSagaStatus status) {
        try {
            blueprintRepository.findById(blueprintId)
                    .map(blueprint -> {
                        blueprint.setBlueprintSagaStatus(status);
                        return blueprint;
                    })
                    .map(blueprintRepository::save)
                    .ifPresent(updatedBlueprint -> log.info("Updated status to {} of blueprint: {}", status, updatedBlueprint));
        } catch (Exception e) {
            log.error("Error updating status to {} for blueprintId: {} – error: {}", status, blueprintId, e.getMessage(), e);
        }
    }
}