package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.blueprint_service.domain.model.BlueprintSagaStatus;
import com.wenox.anonymization.blueprint_service.domain.ports.DumpRepository;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultBlueprintSagaService implements BlueprintSagaService {

    private final BlueprintSagaStatusUpdater blueprintSagaStatusUpdater;
    private final DumpRepository dumpRepository;

    @Override
    public void handle(DatabaseRestoredSuccessEvent event) {
        blueprintSagaStatusUpdater.updateBlueprintSagaStatus(event.getBlueprintId(), BlueprintSagaStatus.RESTORE_SUCCESS);
    }

    @Override
    public void handle(DatabaseRestoredFailureEvent event) {
        dumpRepository.deleteDump(event.getDatabaseName());
        blueprintSagaStatusUpdater.updateBlueprintSagaStatus(event.getBlueprintId(), BlueprintSagaStatus.RESTORE_FAILURE);
    }

    @Override
    public void handle(MetadataExtractedSuccessEvent event) {
        blueprintSagaStatusUpdater.updateBlueprintSagaStatus(event.getBlueprintId(), BlueprintSagaStatus.METADATA_EXTRACTION_SUCCESS);
    }

    @Override
    public void handle(MetadataExtractedFailureEvent event) {
        dumpRepository.deleteDump(event.getDatabaseName());
        blueprintSagaStatusUpdater.updateBlueprintSagaStatus(event.getBlueprintId(), BlueprintSagaStatus.METADATA_EXTRACTION_FAILURE);
    }
}
