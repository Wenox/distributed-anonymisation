package com.wenox.anonymization.metadata_extraction_service.adapters.inbound.messaging;

import com.wenox.anonymization.metadata_extraction_service.domain.service.messaging.DatabaseRestoredService;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.ShutdownSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseRestoredKafkaListener {

    private final DatabaseRestoredService databaseRestoredService;

    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_SUCCESS, groupId = "metadata-extraction-service-group")
    void onRestoreSuccess(DatabaseRestoredSuccessEvent event, Acknowledgment ack) {
        log.info("Received {}", event);
        databaseRestoredService.handle(event);
        ack.acknowledge();
    }
}
