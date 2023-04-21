package com.wenox.anonymization.worksheet_service.operation.shuffle;

import com.wenox.anonymization.worksheet_service.operation.base.AddOperationRequest;
import com.wenox.anonymization.worksheet_service.operation.OperationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddShuffleRequest extends AddOperationRequest {

    @NotNull
    @Valid
    private ShuffleSettings settings;

    @Override
    public OperationType getOperationType() {
        return OperationType.SHUFFLE;
    }

    @Override
    public ShuffleSettings getSettings() {
        return settings;
    }
}
