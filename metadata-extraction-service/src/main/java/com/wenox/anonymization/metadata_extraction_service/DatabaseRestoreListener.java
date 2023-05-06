package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.metadata_extraction_service.domain.Metadata;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseRestoreListener implements RestoreListener {

    private final MetadataExtractor metadataExtractor;
    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;
    private final MetadataRepository metadataRepository;
    private final RetryTemplate retryTemplate;

    @Override
    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_SUCCESS, groupId = "metadata-extraction-service-group")
    public void onRestoreSuccess(DatabaseRestoredSuccessEvent event) {
        log.info("Received {}", event);
        try {
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                DatabaseConnection connection = DatabaseConnection.forPostgres(event.getDatabaseName());
                final Metadata metadata = metadataExtractor.extractMetadata(connection);
                metadata.setBlueprintId(event.getBlueprintId());
                log.info("Saving metadata: {}", metadata);
                metadataRepository.save(metadata);
                loggingKafkaTemplate.send(KafkaConstants.TOPIC_METADATA_SUCCESS, new MetadataExtractedSuccessEvent(event.getBlueprintId()));
                return null;
            });
        } catch (final Exception ex) {
            log.error("Error during metadata extraction for {}", event, ex);
            loggingKafkaTemplate.send(KafkaConstants.TOPIC_METADATA_FAILURE, new MetadataExtractedFailureEvent(event.getBlueprintId(), ex.getMessage(), ex));
        }
    }

    @Override
    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_FAILURE, groupId = "metadata-extraction-service-group")
    public void onRestoreFailure(DatabaseRestoredFailureEvent event) {
        log.info("Received {}", event);
    }
}
