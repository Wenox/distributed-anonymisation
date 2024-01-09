package com.wenox.anonymization.shared_events_library;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class WorksheetCreatedEvent {
    String worksheetId;
    String blueprintId;
    RestoreMode restoreMode;
}

