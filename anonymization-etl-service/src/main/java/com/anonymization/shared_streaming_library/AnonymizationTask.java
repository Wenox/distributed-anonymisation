package com.anonymization.shared_streaming_library;

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
