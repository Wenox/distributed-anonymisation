package com.wenox.anonymization.blueprint_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultBlueprintService implements BlueprintService {

    private final BlueprintRepository blueprintRepository;
    private final S3UploadHandler s3UploadHandler;
    private final BlueprintStatusUpdater blueprintStatusUpdater;
    private final BlueprintMapper blueprintMapper;

    @Override
    public String importBlueprint(ImportBlueprintRequest dto) {
        Blueprint blueprint = blueprintMapper.fromImportRequest(dto);
        blueprintRepository.save(blueprint);

        byte[] content;
        try {
            content = dto.dumpFile().getBytes();
        } catch (IOException ex) {
            log.error("Error when retrieving dump content for dto: {}", dto, ex);
            blueprintStatusUpdater.updateBlueprintStatusOnFailure(blueprint);
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
        if (s3UploadHandler.uploadToS3(content, blueprint)) {
            blueprintStatusUpdater.updateBlueprintStatusOnSuccess(blueprint);
        } else {
            blueprintStatusUpdater.updateBlueprintStatusOnFailure(blueprint);
        }
    }

    @Override
    public Blueprint getBlueprint(String blueprintId) {
        return blueprintRepository.findById(blueprintId)
                .orElseThrow(() -> new BlueprintNotFoundException("Blueprint not found with blueprintId: " + blueprintId));
    }
}
