package com.wenox.anonymization.worksheet_service.operation.base;

import com.wenox.anonymization.worksheet_service.operation.OperationType;
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
