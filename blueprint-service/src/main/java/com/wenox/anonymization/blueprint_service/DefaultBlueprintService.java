package com.wenox.anonymization.blueprint_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        CompletableFuture.runAsync(() -> {
            if (s3UploadHandler.uploadToS3(dto, blueprint)) {
                blueprintStatusUpdater.updateBlueprintStatusOnSuccess(blueprint);
            } else {
                blueprintStatusUpdater.updateBlueprintStatusOnFailure(blueprint);
            }
        }).exceptionally(ex -> {
            log.error("Error for blueprint {} while updating blueprint after S3 upload.", blueprint, ex);
            return null;
        });

        return blueprint.getBlueprintId();
    }

    @Override
    public Blueprint getBlueprint(String blueprintId) {
        return blueprintRepository.findById(blueprintId)
                .orElseThrow(() -> new BlueprintNotFoundException("Blueprint not found with blueprintId: " + blueprintId));
    }
}
