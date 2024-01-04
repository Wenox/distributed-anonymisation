package com.wenox.anonymization.blueprint_service.adapters.inbound.messaging;

import com.wenox.anonymization.blueprint_service.domain.service.BlueprintSagaService;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DatabaseRestoredKafkaListener {

    private final BlueprintSagaService blueprintSagaService;

    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_SUCCESS, groupId = "blueprint-service-group")
    void onRestoreSuccess(DatabaseRestoredSuccessEvent event, Acknowledgment ack) {
        log.info("Received {}", event);
        blueprintSagaService.handle(event);
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_FAILURE, groupId = "blueprint-service-group")
    void onRestoreFailure(DatabaseRestoredFailureEvent event) {
        log.info("-----> Started compensating transaction: delete file from s3,  {}", event);
        blueprintSagaService.handle(event);
        log.info("<----- Finished compensating transaction, delete file from s3, {}", event);
    }
}
