package com.wenox.anonymization.worksheet_service.operation;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public abstract class AddOperationRequest {

    @NotEmpty
    private String table;

    @NotEmpty
    private String column;

    public abstract OperationType getOperationType();

    public abstract OperationSettings getSettings();
}
