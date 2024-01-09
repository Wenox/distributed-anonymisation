package com.wenox.anonymization.metadata_extraction_service.domain.service.messaging;

import com.wenox.anonymization.metadata_extraction_service.domain.model.DatabaseConnection;
import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;
import com.wenox.anonymization.metadata_extraction_service.domain.port.MessagePublisher;
import com.wenox.anonymization.metadata_extraction_service.domain.port.MetadataRepository;
import com.wenox.anonymization.metadata_extraction_service.domain.service.MetadataExtractionService;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class DatabaseRestoredService {

    private final MetadataRepository metadataRepository;
    private final MetadataExtractionService metadataExtractionService;
    private final MessagePublisher messagePublisher;

    public void handle(DatabaseRestoredSuccessEvent event) {
        try {
            DatabaseConnection connection = DatabaseConnection.forPostgres(event.getDatabaseName());
            Metadata metadata = metadataExtractionService.extractMetadata(connection);
            metadata.setBlueprintId(event.getBlueprintId());
            metadataRepository.save(metadata);
            messagePublisher.sendMetadataExtractedSuccess(new MetadataExtractedSuccessEvent(event.getBlueprintId()));
        } catch (Exception ex) {
            log.error("Error during metadata extraction for {}", event, ex);
            messagePublisher.sendMetadataExtractedFailure(new MetadataExtractedFailureEvent(event.getBlueprintId(), event.getDatabaseName(), ex.getMessage(), ex));
        }
    }
}
