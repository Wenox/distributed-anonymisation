package com.wenox.anonymization.blueprint_service.adapters.outbound.persistence.dump;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.ports.DumpRepository;
import com.wenox.anonymization.s3.S3Constants;
import com.wenox.anonymization.s3.api.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DumpRepositoryS3Adapter implements DumpRepository {

    private final StorageService s3StorageService;

    @Override
    public boolean uploadDump(byte[] content, Blueprint blueprint) {
        try {
            log.info("Uploading Dump to S3... Blueprint : {}", blueprint);
            s3StorageService.uploadFile(content, S3Constants.BUCKET_BLUEPRINTS, blueprint.getBlueprintDatabaseName());
            return true;
        } catch (Exception e) {
            log.error("Error while uploading dump for Blueprint : {} ", blueprint, e);
            return false;
        }
    }

    @Override
    public void deleteDump(String databaseName) {
        log.info("Deleting Dump from S3... Database : {}", databaseName);
        s3StorageService.deleteFile(S3Constants.BUCKET_BLUEPRINTS, databaseName);
    }
}
