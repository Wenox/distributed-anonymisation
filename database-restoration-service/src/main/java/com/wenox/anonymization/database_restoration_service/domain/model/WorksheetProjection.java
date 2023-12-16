package com.wenox.anonymization.database_restoration_service.domain.model;

import com.wenox.anonymization.shared_events_library.WorksheetCreatedEvent;
import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorksheetProjection {

    private String worksheetId;

    private String blueprintId;

    private String databaseName;

    private RestoreMode restoreMode;

    public static WorksheetProjection from(WorksheetCreatedEvent event) {
        WorksheetProjection projection = new WorksheetProjection();
        projection.setWorksheetId(event.getWorksheetId());
        projection.setBlueprintId(event.getBlueprintId());
        projection.setDatabaseName(event.getDatabaseName());
        projection.setRestoreMode(event.getRestoreMode());
        return projection;
    }
}

