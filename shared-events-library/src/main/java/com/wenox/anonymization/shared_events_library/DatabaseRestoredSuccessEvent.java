package com.wenox.anonymization.shared_events_library;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class DatabaseRestoredSuccessEvent {
    String blueprintId;
}
