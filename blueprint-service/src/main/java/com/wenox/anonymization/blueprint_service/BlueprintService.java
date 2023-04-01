package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlueprintService {

    private final BlueprintRepository blueprintRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public String importBlueprint(ImportBlueprintRequest dto) throws IOException {
        log.info("Importing blueprint service...");
        final var blueprint = new Blueprint();
        blueprint.setBlueprintDatabaseName("db-" + UUID.randomUUID());
        blueprint.setTitle(dto.title());
        blueprint.setDescription(dto.description());
        blueprint.setCreatedDate(LocalDateTime.now());

        blueprint.setOriginalDumpName(dto.dumpFile().getOriginalFilename());
        blueprint.setFileContent(dto.dumpFile().getBytes());
        blueprint.setContentType(dto.dumpFile().getContentType());

        log.info("Saving : {}", blueprint);
        blueprintRepository.save(blueprint);

        log.info("Sending to kafka");
        kafkaTemplate.send("wenox-blueprints", blueprint.getBlueprintDatabaseName());
        log.info("Sent to kafka");

        return blueprint.getBlueprintId();
    }
}
