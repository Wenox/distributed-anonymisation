package com.wenox.anonymization.worksheet_service.operation.suppression;

import com.wenox.anonymization.worksheet_service.operation.base.OperationSettings;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SuppressionSettings implements OperationSettings {

    @NotEmpty
    private String token;
}
