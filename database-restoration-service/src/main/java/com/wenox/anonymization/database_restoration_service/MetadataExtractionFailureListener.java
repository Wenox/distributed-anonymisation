package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.impl.LoggingKafkaTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MetadataExtractionFailureListener {

    private final DropDatabaseService dropDatabaseService;
    private final ExistsDatabaseService existsDatabaseService;
    private final LoggingKafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaConstants.TOPIC_METADATA_FAILURE, groupId = "blueprint-service-group")
    @Retryable(retryFor = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void onExtractFailure(MetadataExtractedFailureEvent event) {
        log.info("-----> Received compensating transaction {}", event);
        String db = event.getDatabaseName();
        try {
            if (existsDatabaseService.existsDatabase(db)) {
                dropDatabaseService.dropDatabase(db);
            }
            kafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_FAILURE, event);
        } catch (Exception ex) {
            log.error("Error occurred during execution of compensating transaction for {}", event, ex);
        }
        log.info("<----- Finished compensating transaction {}", event);
    }
}
