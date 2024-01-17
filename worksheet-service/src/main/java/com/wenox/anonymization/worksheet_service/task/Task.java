package com.wenox.anonymization.worksheet_service.task;

import com.wenox.anonymization.worksheet_service.operation.OperationType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@Builder
public class Task {
    private OperationType type;
    private String taskId;
    private String worksheetId;
    private String blueprintId;
    private String tableName;
    private String columnName;
    private String columnType;
    private String primaryKey;
    private String primaryKeyType;
    private Map<String, String> configuration;
}
