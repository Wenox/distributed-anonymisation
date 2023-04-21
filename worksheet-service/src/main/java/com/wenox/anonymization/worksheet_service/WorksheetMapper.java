package com.wenox.anonymization.worksheet_service;

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
        log.info("Creating worksheet : {}", worksheet);
        return worksheet;
    }

    public WorksheetResponse toResponse(Worksheet worksheet) {
        return WorksheetResponse.builder()
                .worksheetId(worksheet.getWorksheetId())
                .blueprintId(worksheet.getBlueprintId())
                .worksheetStatus(worksheet.getWorksheetStatus())
                .metadata(worksheet.getMetadata())
                .worksheetName(worksheet.getWorksheetName())
                .databaseName(worksheet.getDatabaseName())
                .build();
    }
}
