package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.s3.S3Constants;
import com.wenox.anonymization.s3.api.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3UploadHandler {
    private final StorageService s3StorageService;

    public boolean uploadToS3(ImportBlueprintRequest dto, Blueprint blueprint) {
        try {
            log.info("Uploading to S3... Dump: {}, blueprintId: {}", dto.dumpFile().getOriginalFilename(), blueprint.getBlueprintId());
            s3StorageService.uploadFile(dto.dumpFile(), S3Constants.BUCKET_BLUEPRINTS, blueprint.getBlueprintDatabaseName());
            return true;
        } catch (Exception e) {
            log.error("Error while storing dump: ", e);
            return false;
        }
    }
}