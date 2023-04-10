package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.CreateWorksheetResponse;
import com.wenox.anonymization.worksheet_service.domain.Worksheet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WorksheetMapper {

    public Worksheet toWorksheet(CreateWorksheetRequest request, CreateWorksheetResponse response) {
        Worksheet worksheet = new Worksheet();
        worksheet.setMetadata(response.metadata());
        worksheet.setBlueprintId(request.blueprintId());
        worksheet.setWorksheetName(request.worksheetName());
        worksheet.setDatabaseName(response.blueprint().blueprintDatabaseName());
        log.info("Creating worksheet : {}", worksheet);
        return worksheet;
    }
}
