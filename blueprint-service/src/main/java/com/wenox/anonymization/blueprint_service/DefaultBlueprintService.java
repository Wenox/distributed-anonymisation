package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.s3_file_manager.KafkaConstants;
import com.wenox.anonymization.s3_file_manager.S3Constants;
import com.wenox.anonymization.s3_file_manager.api.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultBlueprintService implements BlueprintService {

    private final BlueprintRepository blueprintRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final StorageService s3StorageService;

    public String importBlueprint(ImportBlueprintRequest dto) {
        Blueprint blueprint = createBlueprint(dto);
        blueprintRepository.save(blueprint);

        CompletableFuture.runAsync(() -> uploadToS3AndUpdateBlueprint(dto, blueprint))
                .exceptionally(ex -> {
                    log.error("Error for blueprint {} in S3 dump upload: {}", blueprint, ex);
                    return null;
                });

        return blueprint.getBlueprintId();
    }

    private Blueprint createBlueprint(ImportBlueprintRequest dto) {
        Blueprint blueprint = new Blueprint();
        blueprint.setBlueprintStatus(BlueprintStatus.CREATED);
        blueprint.setBlueprintDatabaseName("db-" + UUID.randomUUID()); // todo include timestamp in name
        blueprint.setTitle(dto.title());
        blueprint.setDescription(dto.description());
        blueprint.setCreatedDate(LocalDateTime.now());
        blueprint.setOriginalDumpName(dto.dumpFile().getOriginalFilename());
        return blueprint;
    }

    private void uploadToS3AndUpdateBlueprint(ImportBlueprintRequest dto, Blueprint blueprint) {
        try {
            log.info("Uploading to S3... Dump: {}, blueprintId: {}", dto.dumpFile().getOriginalFilename(), blueprint.getBlueprintId());
            s3StorageService.uploadFile(S3Constants.BUCKET_BLUEPRINTS, blueprint.getBlueprintDatabaseName(), dto.dumpFile().getInputStream());
            blueprint.setDumpStoreSuccess(true);
            blueprint.setBlueprintStatus(BlueprintStatus.DUMP_STORE_SUCCESS);
        } catch (Exception e) {
            log.error("Error while storing dump: ", e);
            blueprint.setBlueprintStatus(BlueprintStatus.DUMP_STORE_FAILURE);
            blueprint.setDumpStoreSuccess(false);
        }

        blueprintRepository.save(blueprint);
        kafkaTemplate.send(KafkaConstants.TOPIC_BLUEPRINTS, blueprint.getBlueprintDatabaseName());
    }
}
