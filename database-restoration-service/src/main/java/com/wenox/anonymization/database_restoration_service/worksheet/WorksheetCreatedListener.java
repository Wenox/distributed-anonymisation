package com.wenox.anonymization.database_restoration_service.worksheet;

import com.wenox.anonymization.shared_events_library.WorksheetCreatedEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorksheetCreatedListener {

    private final WorksheetProjectionRepository repository;
    private final WorksheetProjectionMapper mapper;

    @KafkaListener(topics = KafkaConstants.TOPIC_CREATED_WORKSHEET, groupId = "database-restoration-service-group")
    public void onWorksheetCreated(WorksheetCreatedEvent event) {
        log.info("Received {}", event);
        WorksheetProjection projection = mapper.toProjection(event);
        repository.save(projection);
    }
}
