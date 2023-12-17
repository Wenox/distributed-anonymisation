package com.wenox.anonymization.metadata_extraction_service.domain.port;

import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;

public interface MessagePublisher {

    void sendMetadataExtractedSuccess(MetadataExtractedSuccessEvent event);

    void sendMetadataExtractedFailure(MetadataExtractedFailureEvent event);
}
