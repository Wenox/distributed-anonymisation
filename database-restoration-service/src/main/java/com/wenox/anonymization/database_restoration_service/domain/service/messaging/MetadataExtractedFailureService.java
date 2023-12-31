package com.wenox.anonymization.database_restoration_service.domain.service.messaging;

import com.wenox.anonymization.database_restoration_service.domain.ports.DropDatabasePort;
import com.wenox.anonymization.database_restoration_service.domain.ports.ExistsDatabasePort;
import com.wenox.anonymization.database_restoration_service.domain.ports.MessagePublisher;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MetadataExtractedFailureService {

    private final DropDatabasePort dropDatabasePort;
    private final ExistsDatabasePort existsDatabasePort;
    private final MessagePublisher messagePublisher;

    public void handle(MetadataExtractedFailureEvent event) {
        try {
            if (existsDatabasePort.existsDatabase(event.getDatabaseName())) {
                dropDatabasePort.dropDatabase(event.getDatabaseName());
            }
            messagePublisher.send(event);
        } catch (Exception ex) {
            log.error("Error occurred during execution of compensating transaction for {}", event, ex);
        }
    }
}
