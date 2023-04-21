package com.wenox.anonymization.worksheet_service.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenox.anonymization.worksheet_service.WorksheetMapper;
import com.wenox.anonymization.worksheet_service.domain.Worksheet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class OperationMapper<T extends AddOperationRequest> {

    private final WorksheetMapper worksheetMapper;
    private final ObjectMapper objectMapper;

    public Operation toOperation(Worksheet worksheet, T request) {
        try {
            return Operation.builder()
                    .key(Operation.Key.builder()
                            .worksheetId(worksheet.getWorksheetId())
                            .tableName(request.getTable())
                            .columnName(request.getColumn())
                            .operationType(request.getOperationType())
                            .build())
                    .settings(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request.getSettings()))
                    .build();
        } catch (JsonProcessingException ex) {
            log.error("Error converting settings object : {} for request : {} and worksheet : {}", request.getSettings(), request, worksheet, ex);
            throw new RuntimeException("Error converting object to JSON for worksheetId: " + worksheet.getWorksheetId(), ex);
        }
    }

    public AddOperationResponse toResponse(Operation operation, Worksheet worksheet) {
        try {
            return AddOperationResponse.builder()
                    .worksheet(worksheetMapper.toResponse(worksheet))
                    .worksheetId(operation.getKey().getWorksheetId())
                    .tableName(operation.getKey().getTableName())
                    .columnName(operation.getKey().getColumnName())
                    .operationType(operation.getKey().getOperationType())
                    .columnType(operation.getColumnType())
                    .settings(objectMapper.readValue(operation.getSettings(), Object.class))
                    .build();
        } catch (JsonProcessingException ex) {
            log.error("Error parsing settings json for operation : {} and worksheet : {}", operation, worksheet, ex);
            throw new RuntimeException("Error parsing settings json for operation workshetId: " + worksheet.getWorksheetId(), ex);
        }
    }
}
