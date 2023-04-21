package com.wenox.anonymization.worksheet_service.operation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddSuppressionRequest extends AddOperationRequest {

    @NotNull
    @Valid
    private SuppressionSettings settings;
}
