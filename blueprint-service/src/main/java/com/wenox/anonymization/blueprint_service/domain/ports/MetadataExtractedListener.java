package com.wenox.anonymization.blueprint_service.domain.ports;

import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;

public interface MetadataExtractedListener {

    void onExtractSuccess(MetadataExtractedSuccessEvent event);

    void onExtractFailure(MetadataExtractedFailureEvent event);
}
