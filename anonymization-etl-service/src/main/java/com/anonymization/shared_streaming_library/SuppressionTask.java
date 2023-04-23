package com.anonymization.shared_streaming_library;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SuppressionTask extends AnonymizationTask {
    private SuppressionSettings settings;

    static class SuppressionSettings {
        private String token;
    }
}
