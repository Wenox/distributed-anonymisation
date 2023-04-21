package com.wenox.anonymization.worksheet_service.operation.shuffle;

import com.wenox.anonymization.worksheet_service.operation.base.OperationSettings;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShuffleSettings implements OperationSettings {

    @NotNull
    private Boolean repetitions;
}
