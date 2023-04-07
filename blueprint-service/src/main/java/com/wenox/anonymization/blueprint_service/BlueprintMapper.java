package com.wenox.anonymization.blueprint_service;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class BlueprintMapper {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public Blueprint fromImportRequest(ImportBlueprintRequest dto) {
        Blueprint blueprint = new Blueprint();
        blueprint.setBlueprintStatus(BlueprintStatus.CREATED);
        blueprint.setDumpStoreSuccess(false);
        blueprint.setBlueprintDatabaseName(generateDatabaseName());
        blueprint.setRestoreMode(dto.restoreMode());
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
