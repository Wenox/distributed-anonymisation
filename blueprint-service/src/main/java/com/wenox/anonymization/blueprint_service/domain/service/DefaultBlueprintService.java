package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.blueprint_service.domain.exception.BlueprintNotFoundException;
import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.ports.MessagePublisher;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import com.wenox.anonymization.blueprint_service.domain.ports.DumpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
public class DefaultBlueprintService implements BlueprintService {

    private final BlueprintRepository blueprintRepository;
    private final DumpRepository dumpRepository;
    private final BlueprintSagaStatusUpdater blueprintSagaStatusUpdater;
    private final MessagePublisher messagePublisher;

    @Override
    public Blueprint getBlueprint(String blueprintId) {
        return blueprintRepository.findById(blueprintId)
                .orElseThrow(() -> new BlueprintNotFoundException("Blueprint not found with blueprintId: " + blueprintId));
    }

    @Override
    public String importBlueprint(Blueprint blueprint) {
        blueprintRepository.save(blueprint);

        byte[] content;
        try {
            content = blueprint.getDumpFile().getBytes();
        } catch (IOException ex) {
            log.error("Error when retrieving dump content for dto: {}", blueprint, ex);
            blueprintSagaStatusUpdater.updateSagaStatusOnDumpStoreFailure(blueprint);
            return blueprint.getBlueprintId();
        }

        CompletableFuture.runAsync(() -> handleUploadAndStatus(content, blueprint))
                .exceptionally(ex -> {
                    log.error("Error for blueprint {} while updating blueprint after S3 upload.", blueprint, ex);
                    return null;
                });

        return blueprint.getBlueprintId();
    }

    private void handleUploadAndStatus(byte[] content, Blueprint blueprint) {
        if (dumpRepository.uploadDump(content, blueprint)) {
            blueprintSagaStatusUpdater.updateSagaStatusOnDumpStoreSuccess(blueprint);
            messagePublisher.sendBlueprintCreated(blueprint);
        } else {
            blueprintSagaStatusUpdater.updateSagaStatusOnDumpStoreFailure(blueprint);
        }
    }
}
