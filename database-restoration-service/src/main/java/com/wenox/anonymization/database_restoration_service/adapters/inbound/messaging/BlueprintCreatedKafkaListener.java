package com.wenox.anonymization.database_restoration_service.adapters.inbound.messaging;

import com.wenox.anonymization.database_restoration_service.domain.service.messaging.BlueprintCreatedService;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class BlueprintCreatedKafkaListener {

    private final BlueprintCreatedService blueprintCreatedService;

    @KafkaListener(topics = KafkaConstants.TOPIC_CREATED_BLUEPRINT, groupId = "database-restoration-service-group")
    void onBlueprintCreated(BlueprintCreatedEvent event, Acknowledgment ack) {
        log.info("Received {}", event);
        blueprintCreatedService.handle(event);
        ack.acknowledge();
    }
}