package com.wenox.anonymization.database_restoration_service.adapters.kafka_listener;

import com.wenox.anonymization.database_restoration_service.domain.service.restoration_lifecycle.RestorationLifecycleService;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration.DefaultRestorationService;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// todo uniform logging for events, rest
// todo uniform topics names, events names
// todo uniform dto naming
@Slf4j
@Component
@RequiredArgsConstructor
class BlueprintCreatedEventListener {

    private final RestorationLifecycleService restorationDelegate;
    private final DefaultRestorationService defaultRestorationService;
    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;

    @KafkaListener(topics = KafkaConstants.TOPIC_CREATED_BLUEPRINT, groupId = "database-restoration-service-group")
    void onBlueprintCreated(BlueprintCreatedEvent event) {
        log.info("Received {}", event);
        try {
            restorationDelegate.restore(event.getDatabaseName(), event.getRestoreMode());
            defaultRestorationService.saveActiveRestoration(event);
            loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_SUCCESS, new DatabaseRestoredSuccessEvent(event.getBlueprintId(), event.getDatabaseName()));
        } catch (Exception ex) {
            log.error("Error during database restoration for {}", event, ex);
            defaultRestorationService.saveInactiveRestoration(event);
            loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_FAILURE, new DatabaseRestoredFailureEvent(event.getBlueprintId(), event.getDatabaseName(), ex.getMessage(), ex));
        }
    }
}
