package com.wenox.anonymization.database_restoration_service.domain.ports;

import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;

public interface MessagePublisher {

    void send(DatabaseRestoredSuccessEvent event);

    void send(DatabaseRestoredFailureEvent event);

    void send(MetadataExtractedFailureEvent event);
}
