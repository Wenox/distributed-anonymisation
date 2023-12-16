package com.wenox.anonymization.database_restoration_service.domain.ports;

import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;

public interface MessagePublisher {

    void sendDatabaseRestoredSuccess(String blueprintId, String databaseName);

    void sendDatabaseRestoredFailure(String blueprintId, String databaseName, String errorMessage, Exception ex);

    void sendMetadataExtractedFailure(MetadataExtractedFailureEvent event);
}
