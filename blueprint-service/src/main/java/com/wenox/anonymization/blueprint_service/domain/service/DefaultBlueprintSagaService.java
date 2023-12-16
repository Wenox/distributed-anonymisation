package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.blueprint_service.domain.model.BlueprintStatus;
import com.wenox.anonymization.blueprint_service.domain.ports.DumpRepository;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultBlueprintSagaService implements BlueprintSagaService {

    private final BlueprintStatusUpdater blueprintStatusUpdater;
    private final DumpRepository dumpRepository;

    @Override
    public void handleRestorationSuccess(DatabaseRestoredSuccessEvent event) {
        blueprintStatusUpdater.updateBlueprintStatus(event.getBlueprintId(), BlueprintStatus.RESTORE_SUCCESS);
    }

    @Override
    public void handleRestorationFailure(DatabaseRestoredFailureEvent event) {
        blueprintStatusUpdater.updateBlueprintStatus(event.getBlueprintId(), BlueprintStatus.RESTORE_FAILURE);
        dumpRepository.deleteDump(event.getDatabaseName());
    }

    @Override
    public void handleExtractionSuccess(MetadataExtractedSuccessEvent event) {
        blueprintStatusUpdater.updateBlueprintStatus(event.getBlueprintId(), BlueprintStatus.METADATA_EXTRACTION_SUCCESS);
    }

    @Override
    public void handleExtractionFailure(MetadataExtractedFailureEvent event) {
        blueprintStatusUpdater.updateBlueprintStatus(event.getBlueprintId(), BlueprintStatus.METADATA_EXTRACTION_FAILURE);
    }
}
