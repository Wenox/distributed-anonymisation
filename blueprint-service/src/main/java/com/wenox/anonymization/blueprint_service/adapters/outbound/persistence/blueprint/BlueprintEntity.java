package com.wenox.anonymization.blueprint_service.adapters.outbound.persistence.blueprint;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.model.BlueprintSagaStatus;
import com.wenox.anonymization.blueprint_service.domain.model.DatabaseType;
import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document
class BlueprintEntity {

    @Id
    private String blueprintId;

    private BlueprintSagaStatus blueprintSagaStatus;

    private RestoreMode restoreMode;

    private DatabaseType databaseType;

    private String title;

    private String blueprintDatabaseName;

    private boolean dumpStoreSuccess;

    private String description;

    private LocalDateTime createdDate;

    private String originalDumpName;

    static BlueprintEntity fromDomain(Blueprint blueprint) {
        BlueprintEntity entity = new BlueprintEntity();
        entity.setBlueprintId(blueprint.getBlueprintId());
        entity.setBlueprintSagaStatus(blueprint.getBlueprintSagaStatus());
        entity.setRestoreMode(blueprint.getRestoreMode());
        entity.setDatabaseType(blueprint.getDatabaseType());
        entity.setTitle(blueprint.getTitle());
        entity.setBlueprintDatabaseName(blueprint.getBlueprintDatabaseName());
        entity.setDumpStoreSuccess(blueprint.isDumpStoreSuccess());
        entity.setDescription(blueprint.getDescription());
        entity.setCreatedDate(blueprint.getCreatedDate());
        entity.setOriginalDumpName(blueprint.getOriginalDumpName());
        return entity;
    }

    Blueprint toDomain() {
        Blueprint blueprint = new Blueprint();
        blueprint.setBlueprintId(this.getBlueprintId());
        blueprint.setBlueprintSagaStatus(this.getBlueprintSagaStatus());
        blueprint.setRestoreMode(this.getRestoreMode());
        blueprint.setDatabaseType(this.getDatabaseType());
        blueprint.setTitle(this.getTitle());
        blueprint.setBlueprintDatabaseName(this.getBlueprintDatabaseName());
        blueprint.setDumpStoreSuccess(this.isDumpStoreSuccess());
        blueprint.setDescription(this.getDescription());
        blueprint.setCreatedDate(this.getCreatedDate());
        blueprint.setOriginalDumpName(this.getOriginalDumpName());
        blueprint.setDumpFile(null);
        return blueprint;
    }
}
