package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.WorksheetResponse;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AddOperationResponse {

    private String worksheetId;

    private String tableName;

    private String columnName;

    private OperationType operationType;

    private String columnType;

    private String settings;

    WorksheetResponse worksheet;
}
