package com.anonymization.etl.domain.tasks;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ShuffleTask extends Task {
    private Boolean repetitions;
}
