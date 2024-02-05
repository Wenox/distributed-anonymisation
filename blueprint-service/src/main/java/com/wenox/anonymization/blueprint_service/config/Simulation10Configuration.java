package com.wenox.anonymization.blueprint_service.config;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.model.BlueprintSagaStatus;
import com.wenox.anonymization.blueprint_service.domain.model.DatabaseType;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@Slf4j
public class Simulation10Configuration {

    @Bean
    public CommandLineRunner loadData(BlueprintRepository repository) {
        return args -> {
            Blueprint blueprint = new Blueprint();
            blueprint.setTitle("Title");
            blueprint.setBlueprintId("1234");
            blueprint.setBlueprintSagaStatus(BlueprintSagaStatus.METADATA_EXTRACTION_SUCCESS);
            blueprint.setCreatedDate(LocalDateTime.now());
            blueprint.setDescription("Description");
            blueprint.setDatabaseType(DatabaseType.POSTGRESQL);
            blueprint.setOriginalDumpName("original-file.sql");
            repository.save(blueprint);
            log.info("Created a blueprint");
        };
    }
}
