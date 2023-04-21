package com.wenox.anonymization.worksheet_service.operation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddSuppressionRequest extends AddOperationRequest {

    private SuppressionSettings settings;
}
