package com.anonymization.etl.domain.tasks;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShuffleTask extends AnonymizationTask {
    private Boolean repetitions;
}
