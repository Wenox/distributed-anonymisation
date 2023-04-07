package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;

public interface ExtractListener {

    void onExtractSuccess(MetadataExtractedSuccessEvent event);

    void onExtractFailure(MetadataExtractedFailureEvent event);
}
