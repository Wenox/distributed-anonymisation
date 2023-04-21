package com.wenox.anonymization.worksheet_service.operation;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddOperationRequest {

    @NotEmpty
    private String table;

    @NotEmpty
    private String column;
}
