package com.wenox.anonymization.worksheet_service.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wenox.anonymization.worksheet_service.ToStringSerializer;
import com.wenox.anonymization.worksheet_service.domain.Table;
import com.wenox.anonymization.worksheet_service.domain.Worksheet;
import com.wenox.anonymization.worksheet_service.operation.base.AddOperationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class OperationMapper<T extends AddOperationRequest> {

    private final ObjectMapper objectMapper;
    private final ObjectMapper stringObjectMapper;

    public OperationMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.stringObjectMapper = createStringObjectMapper();
    }

    private ObjectMapper createStringObjectMapper() {
        ObjectMapper customObjectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Boolean.class, new ToStringSerializer());
        module.addSerializer(Integer.class, new ToStringSerializer());
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(Double.class, new ToStringSerializer());
        module.addSerializer(Float.class, new ToStringSerializer());
        customObjectMapper.registerModule(module);
        return customObjectMapper;
    }

    public Operation toOperation(Worksheet worksheet, T request) {
        try {
            Table table = worksheet.getMetadata().tables().get(request.getTable());
            return Operation
                    .builder()
                    .key(OperationKey
                            .builder()
                            .taskId(buildTaskId(request, worksheet.getWorksheetId()))
                            .worksheetId(worksheet.getWorksheetId())
                            .build())
                    .status(TaskStatus.INITIALIZED)
                    .table(request.getTable())
                    .column(request.getColumn())
                    .operationType(request.getOperationType())
                    .settings(stringObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request.getSettings()))
                    .primaryKey(table.getPrimaryKey().columnName())
                    .primaryKeyType(table.getPrimaryKey().type())
                    .columnType(table.getColumns().get(request.getColumn()).getType())
                    .build();
        } catch (JsonProcessingException ex) {
            log.error("Error converting settings object : {} for request : {} and worksheet : {}", request.getSettings(), request, worksheet, ex);
            throw new RuntimeException("Error converting object to JSON for worksheetId: " + worksheet.getWorksheetId(), ex);
        }
    }

    /**
     * PK serialization. Example key: SUPPRESSION:employees:salary:worksheet-id
     */
    public String buildTaskId(T request, String worksheetId) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getOperationType())
                .append(':')
                .append(request.getTable())
                .append(':')
                .append(request.getColumn())
                .append(':')
                .append(worksheetId);
        return sb.toString();
    }

    public AddOperationResponse toResponse(Operation operation, Worksheet worksheet) {
        try {
            return AddOperationResponse.builder()
                    .status(TaskStatus.INITIALIZED)
                    .taskId(operation.getKey().getTaskId())
                    .worksheetId(operation.getKey().getWorksheetId())
                    .table(operation.getTable())
                    .column(operation.getColumn())
                    .operationType(operation.getOperationType())
                    .columnType(operation.getColumnType())
                    .primaryKey(operation.getPrimaryKey())
                    .primaryKeyType(operation.getPrimaryKeyType())
                    .settings(objectMapper.readValue(operation.getSettings(), Object.class))
                    .build();
        } catch (JsonProcessingException ex) {
            log.error("Error parsing settings json for operation : {} and worksheet : {}", operation, worksheet, ex);
            throw new RuntimeException("Error parsing settings json for operation workshetId: " + worksheet.getWorksheetId(), ex);
        }
    }
}
