package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;

public interface BlueprintSagaService {

    void handleRestorationSuccess(DatabaseRestoredSuccessEvent event);

    void handleRestorationFailure(DatabaseRestoredFailureEvent event);

    void handleExtractionSuccess(MetadataExtractedSuccessEvent event);

    void handleExtractionFailure(MetadataExtractedFailureEvent event);
}