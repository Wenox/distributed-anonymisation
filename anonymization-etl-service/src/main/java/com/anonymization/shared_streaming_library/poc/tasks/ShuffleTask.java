package com.anonymization.shared_streaming_library.poc.tasks;

import com.anonymization.shared_streaming_library.AnonymizationTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShuffleTask extends AnonymizationTask {
    private Boolean repetitions;
}
