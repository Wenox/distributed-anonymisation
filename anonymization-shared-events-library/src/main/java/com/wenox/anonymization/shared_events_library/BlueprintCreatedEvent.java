package com.wenox.anonymization.shared_events_library;

import lombok.*;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class BlueprintCreatedEvent {
    String blueprintId;
    String databaseName;
}
