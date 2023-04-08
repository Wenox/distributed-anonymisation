package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class BlueprintCreatedEventListener {

    private final RestorationDelegate restorationDelegate;
    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;

    @KafkaListener(topics = KafkaConstants.TOPIC_CREATE_BLUEPRINT, groupId = "database-restoration-service-group")
    public void onBlueprintCreated(BlueprintCreatedEvent event) {
        log.info("Received {}", event);
        try {
            restorationDelegate.restore(event.getDatabaseName(), event.getRestoreMode());
            loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_SUCCESS, new DatabaseRestoredSuccessEvent(event.getBlueprintId(), event.getDatabaseName()));
        } catch (Exception ex) {
            log.error("Error during database restoration for {}", event, ex);
            loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_FAILURE, new DatabaseRestoredFailureEvent(event.getBlueprintId(), event.getDatabaseName(), ex.getMessage(), ex));
        }
    }
}
