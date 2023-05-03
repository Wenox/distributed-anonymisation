package com.wenox.anonymization.database_restoration_service.worksheet;

import com.wenox.anonymization.shared_events_library.WorksheetCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class WorksheetProjectionMapper {

    public WorksheetProjection toProjection(WorksheetCreatedEvent event) {
        WorksheetProjection projection = new WorksheetProjection();
        projection.setWorksheetId(event.getWorksheetId());
        projection.setBlueprintId(event.getBlueprintId());
        projection.setDatabaseName(event.getDatabaseName());
        projection.setRestoreMode(event.getRestoreMode());
        return projection;
    }
}
