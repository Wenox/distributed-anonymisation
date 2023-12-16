package com.wenox.anonymization.blueprint_service.adapters.api;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.model.BlueprintSagaStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
class BlueprintMapper {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    Blueprint fromRequest(ImportBlueprintRequest dto) {
        Blueprint blueprint = new Blueprint();
        blueprint.setBlueprintId(UUID.randomUUID().toString());
        blueprint.setDumpFile(dto.dumpFile());
        blueprint.setBlueprintSagaStatus(BlueprintSagaStatus.CREATED);
        blueprint.setDumpStoreSuccess(false);
        blueprint.setBlueprintDatabaseName(generateDatabaseName());
        blueprint.setRestoreMode(dto.restoreMode());
        blueprint.setDatabaseType(dto.databaseType());
        blueprint.setTitle(dto.title());
        blueprint.setDescription(dto.description());
        blueprint.setCreatedDate(LocalDateTime.now());
        blueprint.setOriginalDumpName(dto.dumpFile().getOriginalFilename());
        return blueprint;
    }

    private String generateDatabaseName() {
        String timestamp = LocalDateTime.now().format(formatter);
        return "db-" + UUID.randomUUID() + "-" + timestamp;
    }
}
