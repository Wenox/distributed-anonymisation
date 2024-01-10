package com.wenox.anonymization.blueprint_service.adapters.inbound.api;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.model.BlueprintSagaStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
class BlueprintMapper {

    Blueprint fromRequest(ImportBlueprintRequest dto) {
        Blueprint blueprint = new Blueprint();
        blueprint.setBlueprintId(UUID.randomUUID().toString());
        blueprint.setDumpFile(dto.dumpFile());
        blueprint.setBlueprintSagaStatus(BlueprintSagaStatus.INITIALIZED);
        blueprint.setDumpStoreSuccess(false);
        blueprint.setRestoreMode(dto.restoreMode());
        blueprint.setDatabaseType(dto.databaseType());
        blueprint.setTitle(dto.title());
        blueprint.setDescription(dto.description());
        blueprint.setCreatedDate(LocalDateTime.now());
        blueprint.setOriginalDumpName(dto.dumpFile().getOriginalFilename());
        return blueprint;
    }
}
