package com.wenox.anonymization.shared_events_library;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.*;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class BlueprintCreatedEvent {
    String blueprintId;
    RestoreMode restoreMode;
}
