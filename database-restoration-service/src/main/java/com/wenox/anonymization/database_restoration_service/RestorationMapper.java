package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class RestorationMapper {

    public Restoration toActiveRestoration(BlueprintCreatedEvent event) {
        return Restoration.builder()
                .blueprintId(event.getBlueprintId())
                .databaseName(event.getDatabaseName())
                .isActive(true)
                .runnerIp("localhost")
                .build();
    }

    public Restoration toInactiveRestoration(BlueprintCreatedEvent event) {
        return Restoration.builder()
                .blueprintId(event.getBlueprintId())
                .databaseName(event.getDatabaseName())
                .isActive(false)
                .runnerIp("localhost")
                .build();
    }
}
