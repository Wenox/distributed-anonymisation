package com.wenox.anonymization.database_restoration_service.adapters.inbound.messaging;

import com.wenox.anonymization.database_restoration_service.domain.model.WorksheetProjection;
import com.wenox.anonymization.database_restoration_service.domain.ports.WorksheetProjectionRepository;
import com.wenox.anonymization.shared_events_library.WorksheetCreatedEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class WorksheetCreatedListener {

    private final WorksheetProjectionRepository worksheetProjectionRepository;

    @KafkaListener(topics = KafkaConstants.TOPIC_CREATED_WORKSHEET, groupId = "database-restoration-service-group")
    void onWorksheetCreated(WorksheetCreatedEvent event) {
        log.info("Received {}", event);
        WorksheetProjection projection = WorksheetProjection.from(event);
        worksheetProjectionRepository.save(projection);
    }
}
