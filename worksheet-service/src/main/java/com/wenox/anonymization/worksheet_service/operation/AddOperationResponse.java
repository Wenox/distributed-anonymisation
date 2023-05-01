package com.wenox.anonymization.worksheet_service.operation;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AddOperationResponse {

    private String worksheetId;

    private TaskStatus status;

    private String table;

    private String column;

    private String columnType;

    private String primaryKey;

    private String primaryKeyType;

    private OperationType operationType;

    private Object settings;
}
