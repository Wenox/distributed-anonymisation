package com.wenox.anonymization.metadata_extraction_service.config;

import com.wenox.anonymization.metadata_extraction_service.adapters.outbound.metadata_extraction.DataSourceFactory;
import com.wenox.anonymization.metadata_extraction_service.domain.port.MetadataRepository;
import com.wenox.anonymization.metadata_extraction_service.domain.service.DefaultMetadataExtractionService;
import com.wenox.anonymization.metadata_extraction_service.domain.service.DefaultMetadataService;
import com.wenox.anonymization.metadata_extraction_service.domain.service.MetadataExtractionService;
import com.wenox.anonymization.metadata_extraction_service.domain.service.MetadataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    MetadataService metadataService(MetadataRepository metadataRepository) {
        return new DefaultMetadataService(metadataRepository);
    }

    @Bean
    MetadataExtractionService metadataExtractionService(DataSourceFactory dataSourceFactory) {
        return new DefaultMetadataExtractionService(dataSourceFactory);
    }
}
