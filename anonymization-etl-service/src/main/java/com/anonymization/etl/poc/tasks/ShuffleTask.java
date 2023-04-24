package com.anonymization.etl.poc.tasks;

import com.anonymization.etl.AnonymizationTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShuffleTask extends AnonymizationTask {
    private Boolean repetitions;
}
