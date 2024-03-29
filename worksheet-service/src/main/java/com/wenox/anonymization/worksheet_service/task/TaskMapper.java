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
public class TaskMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Task toTask(Operation operation, Worksheet worksheet) {

        Map<String, String> configuration = new HashMap<>();
        try {
            configuration = objectMapper.readValue(operation.getSettings(), Map.class);
        } catch (JsonProcessingException ex) {
            log.error("Error deserializing operation settings {} to Map. Using empty settings instead. Operation: {}", operation.getSettings(), operation, ex);
        }

        return Task
                .builder()
                .type(operation.getOperationType())
                .taskId(operation.getKey().getTaskId())
                .worksheetId(worksheet.getWorksheetId())
                .blueprintId(worksheet.getBlueprintId())
                .tableName(operation.getTable())
                .columnName(operation.getColumn())
                .columnType(operation.getColumnType())
                .primaryKey(operation.getPrimaryKey())
                .primaryKeyType(operation.getPrimaryKeyType())
                .configuration(configuration)
                .build();
    }
}
