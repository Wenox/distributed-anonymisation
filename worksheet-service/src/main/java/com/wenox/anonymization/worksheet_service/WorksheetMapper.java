package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.shared_events_library.WorksheetCreatedEvent;
import com.wenox.anonymization.worksheet_service.domain.CreateWorksheetResponse;
import com.wenox.anonymization.worksheet_service.domain.Worksheet;
import com.wenox.anonymization.worksheet_service.domain.WorksheetStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WorksheetMapper {

    public Worksheet toWorksheet(CreateWorksheetRequest request, CreateWorksheetResponse response) {
        Worksheet worksheet = new Worksheet();
        worksheet.setWorksheetStatus(WorksheetStatus.OPENED);
        worksheet.setMetadata(response.getMetadata());
        worksheet.setBlueprintId(request.blueprintId());
        worksheet.setWorksheetName(request.worksheetName());
        worksheet.setDatabaseName(response.getBlueprint().blueprintDatabaseName());
        worksheet.setRestoreMode(response.getBlueprint().restoreMode());
        log.info("Creating worksheet : {}", worksheet);
        return worksheet;
    }

    public WorksheetCreatedEvent toWorksheetCreatedEvent(CreateWorksheetResponse response) {
        return WorksheetCreatedEvent
                .builder()
                .worksheetId(response.getWorksheet().getWorksheetId())
                .blueprintId(response.getWorksheet().getBlueprintId())
                .databaseName(response.getWorksheet().getDatabaseName())
                .restoreMode(response.getWorksheet().getRestoreMode())
                .build();
    }

    public WorksheetResponse toResponse(Worksheet worksheet) {
        return WorksheetResponse.builder()
                .worksheetId(worksheet.getWorksheetId())
                .blueprintId(worksheet.getBlueprintId())
                .worksheetStatus(worksheet.getWorksheetStatus())
                .metadata(worksheet.getMetadata())
                .worksheetName(worksheet.getWorksheetName())
                .restoreMode(worksheet.getRestoreMode())
                .databaseName(worksheet.getDatabaseName())
                .build();
    }
}
