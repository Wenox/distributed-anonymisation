package com.anonymization.etl.domain.tasks;

import com.anonymization.etl.domain.OperationType;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class AnonymizationTask {
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
