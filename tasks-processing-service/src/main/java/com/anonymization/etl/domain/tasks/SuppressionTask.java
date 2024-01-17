package com.anonymization.etl.domain.tasks;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SuppressionTask extends Task {
    private String token;
}
