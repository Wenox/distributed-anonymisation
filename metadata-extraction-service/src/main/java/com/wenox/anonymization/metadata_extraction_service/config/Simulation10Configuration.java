package com.wenox.anonymization.metadata_extraction_service.config;


import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;
import com.wenox.anonymization.metadata_extraction_service.domain.port.MetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class Simulation10Configuration {

    @Bean
    public CommandLineRunner loadData(MetadataRepository repository) {
        return args -> {
            Metadata metadata = new Metadata();
            metadata.setBlueprintId("1234");
            metadata.setNumberOfTables(1);
            metadata.setTables(Map.of());
            repository.save(metadata);
            log.info("Created a metadata");
        };
    }
}
