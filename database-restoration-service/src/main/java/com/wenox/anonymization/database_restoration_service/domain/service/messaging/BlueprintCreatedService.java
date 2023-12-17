package com.wenox.anonymization.database_restoration_service.domain.service.messaging;

import com.wenox.anonymization.database_restoration_service.domain.ports.MessagePublisher;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration.RestorationService;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration_lifecycle.RestorationLifecycleService;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class BlueprintCreatedService {

    private final RestorationLifecycleService restorationLifecycleService;
    private final RestorationService restorationService;
    private final MessagePublisher messagePublisher;

    public void handle(BlueprintCreatedEvent event) {
        try {
            restorationLifecycleService.restore(event.getDatabaseName(), event.getRestoreMode());
            restorationService.saveActiveRestoration(event);
            messagePublisher.sendDatabaseRestoredSuccess(new DatabaseRestoredSuccessEvent(event.getBlueprintId(), event.getDatabaseName()));
        } catch (Exception ex) {
            log.error("Error during database restoration for event : {}", event, ex);
            restorationService.saveInactiveRestoration(event);
            messagePublisher.sendDatabaseRestoredFailure(new DatabaseRestoredFailureEvent(event.getBlueprintId(), event.getDatabaseName(), ex.getMessage(), ex));
        }
    }
}
