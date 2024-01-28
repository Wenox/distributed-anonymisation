package com.wenox.anonymization.database_restoration_service.domain.service.messaging;

import com.wenox.anonymization.database_restoration_service.domain.ports.DropDatabasePort;
import com.wenox.anonymization.database_restoration_service.domain.ports.MessagePublisher;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration.RestorationService;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MetadataExtractedFailureService {

    private final DropDatabasePort dropDatabasePort;
    private final RestorationService restorationService;
    private final MessagePublisher messagePublisher;

    public void handle(MetadataExtractedFailureEvent event) {
        try {
            dropDatabasePort.dropDatabase(event.getBlueprintId());
            restorationService.markAsInactive(event.getBlueprintId());
            messagePublisher.send(event);
        } catch (Exception ex) {
            log.error("Error occurred during execution of compensating transaction for {}", event, ex);
            throw new RuntimeException("Error occurred during execution of compensating transaction for: " + event, ex);
        }
    }
}
