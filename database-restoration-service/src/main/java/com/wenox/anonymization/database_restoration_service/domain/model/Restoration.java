package com.wenox.anonymization.database_restoration_service.domain.model;

import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
public class Restoration {

    private String blueprintId;

    private String databaseName;

    private boolean isActive = false;

    private String runnerIp;

    public static Restoration toActiveRestoration(BlueprintCreatedEvent event) {
        return Restoration.builder()
                .blueprintId(event.getBlueprintId())
                .databaseName(event.getDatabaseName())
                .isActive(true)
                .runnerIp("localhost")
                .build();
    }

    public static Restoration toInactiveRestoration(BlueprintCreatedEvent event) {
        return Restoration.builder()
                .blueprintId(event.getBlueprintId())
                .databaseName(event.getDatabaseName())
                .isActive(false)
                .runnerIp("localhost")
                .build();
    }
}
