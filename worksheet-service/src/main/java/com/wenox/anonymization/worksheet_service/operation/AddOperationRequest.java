package com.wenox.anonymization.worksheet_service.operation;

import lombok.Data;

@Data
public class AddOperationRequest {

    private String table;

    private String column;
}
