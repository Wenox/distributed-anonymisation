package com.wenox.anonymization.worksheet_service.operation.generalisation;


import com.wenox.anonymization.worksheet_service.operation.OperationType;
import com.wenox.anonymization.worksheet_service.operation.base.AddOperationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddGeneralisationRequest extends AddOperationRequest {

    @NotNull
    @Valid
    private GeneralisationSettings settings;

    @Override
    public OperationType getOperationType() {
        return OperationType.GENERALISATION;
    }

    @Override
    public GeneralisationSettings getSettings() {
        return settings;
    }
}

