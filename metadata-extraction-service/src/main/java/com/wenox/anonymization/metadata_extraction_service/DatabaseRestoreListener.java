package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseRestoreListener implements RestoreListener {

    @Override
    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_SUCCESS, groupId = "metadata-extraction-service-group")
    public void onRestoreSuccess(DatabaseRestoredSuccessEvent event) {
        log.info("Received {}", event);
    }

    @Override
    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_FAILURE, groupId = "metadata-extraction-service-group")
    public void onRestoreFailure(DatabaseRestoredFailureEvent event) {
        log.info("Received {}", event);
    }
}
