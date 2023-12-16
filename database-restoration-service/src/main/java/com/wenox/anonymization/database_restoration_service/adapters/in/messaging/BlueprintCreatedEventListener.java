package com.wenox.anonymization.database_restoration_service.adapters.in.messaging;

import com.wenox.anonymization.database_restoration_service.domain.service.messaging.BlueprintCreatedService;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class BlueprintCreatedEventListener {

    private final BlueprintCreatedService blueprintCreatedService;

    @KafkaListener(topics = KafkaConstants.TOPIC_CREATED_BLUEPRINT, groupId = "database-restoration-service-group")
    void onBlueprintCreated(BlueprintCreatedEvent event) {
        log.info("Received {}", event);
        blueprintCreatedService.handle(event);
    }
}
// todo uniform logging for events, rest
// todo uniform topics names, events names
// todo uniform dto naming