package com.wenox.anonymization.database_restoration_service.adapters.out.persistence.worksheet;

import com.wenox.anonymization.database_restoration_service.domain.model.WorksheetProjection;
import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document
class WorksheetProjectionEntity {

    @Id
    private String worksheetId;

    private String blueprintId;

    private String databaseName;

    private RestoreMode restoreMode;

    static WorksheetProjection toDomain(WorksheetProjectionEntity entity) {
        WorksheetProjection worksheetProjection = new WorksheetProjection();
        worksheetProjection.setWorksheetId(entity.getWorksheetId());
        worksheetProjection.setBlueprintId(entity.getBlueprintId());
        worksheetProjection.setDatabaseName(entity.getDatabaseName());
        worksheetProjection.setRestoreMode(entity.getRestoreMode());
        return worksheetProjection;
    }

    static WorksheetProjectionEntity fromDomain(WorksheetProjection worksheetProjection) {
        WorksheetProjectionEntity entity = new WorksheetProjectionEntity();
        entity.setWorksheetId(worksheetProjection.getWorksheetId());
        entity.setBlueprintId(worksheetProjection.getBlueprintId());
        entity.setDatabaseName(worksheetProjection.getDatabaseName());
        entity.setRestoreMode(worksheetProjection.getRestoreMode());
        return entity;
    }
}
