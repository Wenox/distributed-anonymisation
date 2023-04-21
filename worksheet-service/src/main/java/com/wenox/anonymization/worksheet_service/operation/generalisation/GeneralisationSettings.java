package com.wenox.anonymization.worksheet_service.operation.generalisation;

import com.wenox.anonymization.worksheet_service.operation.base.OperationSettings;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeneralisationSettings implements OperationSettings {

    private Integer minValue;

    private Integer maxValue;

    private Integer intervalSize;

    private Integer numberOfDistributions;

    @NotNull
    private GeneralisationMode mode;
}
