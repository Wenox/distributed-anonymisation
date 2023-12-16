package com.wenox.anonymization.blueprint_service.adapters.inbound.messaging;

import com.wenox.anonymization.blueprint_service.domain.service.BlueprintSagaService;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DatabaseRestoredKafkaListener {

    private final BlueprintSagaService blueprintSagaService;

    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_SUCCESS, groupId = "blueprint-service-group")
    void onRestoreSuccess(DatabaseRestoredSuccessEvent event) {
        log.info("Received {}", event);
        blueprintSagaService.handle(event);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_RESTORE_FAILURE, groupId = "blueprint-service-group")
    @Retryable(retryFor = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    void onRestoreFailure(DatabaseRestoredFailureEvent event) {
        log.info("-----> Received compensating transaction: delete file from s3,  {}", event);
        blueprintSagaService.handle(event);
        log.info("<----- Finished compensating transaction, delete file from s3, {}", event);
    }
}
