package com.wenox.anonymization.shared_events_library;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AnonymizationScriptReadyEvent {
    private String sagaId;
    private String worksheetId;
    private RestoreMode restoreMode;
    private String databaseName;
}
