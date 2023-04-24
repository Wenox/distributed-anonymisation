package com.anonymization.etl.domain.tasks;

import com.anonymization.etl.domain.OperationType;
import lombok.Data;

@Data
public class AnonymizationTask {
    private OperationType type;
    private String taskId;
    private String worksheetId;
    private String tableName;
    private String columnName;
    private String columnType;
}
