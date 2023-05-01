package com.wenox.anonymization.worksheet_service.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenox.anonymization.worksheet_service.domain.Worksheet;
import com.wenox.anonymization.worksheet_service.operation.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AnonymizationTaskMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnonymizationTask toAnonymizationTask(Operation operation, Worksheet worksheet) {

        Map<String, String> configuration = new HashMap<>();
        try {
            configuration = objectMapper.readValue(operation.getSettings(), Map.class);
        } catch (JsonProcessingException ex) {
            log.error("Error deserializing operation settings {} to Map. Using empty settings instead. Operation: {}", operation.getSettings(), operation, ex);
        }

        return AnonymizationTask
                .builder()
                .type(operation.getKey().getOperationType())
                .taskId(operation.getKey().toString())
                .worksheetId(worksheet.getWorksheetId())
                .blueprintId(worksheet.getBlueprintId())
                .tableName(operation.getKey().getTable())
                .columnName(operation.getKey().getColumn())
                .columnType(operation.getColumnType())
                .primaryKey(operation.getPrimaryKey())
                .primaryKeyType(operation.getPrimaryKeyType())
                .configuration(configuration)
                .build();
    }
}
