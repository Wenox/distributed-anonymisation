package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import com.wenox.anonymization.worksheet_service.domain.CreateWorksheetResponse;
import com.wenox.anonymization.worksheet_service.domain.Worksheet;
import com.wenox.anonymization.worksheet_service.domain.WorksheetCreatedEvent;
import com.wenox.anonymization.worksheet_service.domain.WorksheetStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class WorksheetMapper {

    public Worksheet toWorksheet(String worksheetId, CreateWorksheetRequest request, CreateWorksheetResponse response) {
        Worksheet worksheet = new Worksheet();
        worksheet.setWorksheetId(worksheetId);
        worksheet.setWorksheetStatus(WorksheetStatus.OPENED);
        worksheet.setMetadata(response.getMetadata());
        worksheet.setBlueprintId(request.blueprintId());
        worksheet.setWorksheetName(request.worksheetName());
        worksheet.setRestoreMode(response.getBlueprint().restoreMode());
        log.info("Creating worksheet : {}", worksheet);
        return worksheet;
    }

    public WorksheetCreatedEvent toWorksheetCreatedEvent(String worksheetId, String blueprintId, RestoreMode restoreMode) {
        WorksheetCreatedEvent event = new WorksheetCreatedEvent();
        event.setWorksheetId(worksheetId);
        event.setBlueprintId(blueprintId);
        event.setRestoreMode(restoreMode);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    public WorksheetResponse toResponse(Worksheet worksheet) {
        return WorksheetResponse.builder()
                .worksheetId(worksheet.getWorksheetId())
                .blueprintId(worksheet.getBlueprintId())
                .worksheetStatus(worksheet.getWorksheetStatus())
                .metadata(worksheet.getMetadata())
                .worksheetName(worksheet.getWorksheetName())
                .restoreMode(worksheet.getRestoreMode())
                .build();
    }
}
