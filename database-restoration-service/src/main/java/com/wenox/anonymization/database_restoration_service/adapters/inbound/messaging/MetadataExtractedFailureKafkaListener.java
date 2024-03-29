package com.wenox.anonymization.database_restoration_service.adapters.inbound.messaging;

import com.wenox.anonymization.database_restoration_service.domain.service.messaging.MetadataExtractedFailureService;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
class MetadataExtractedFailureKafkaListener {

    private final MetadataExtractedFailureService metadataExtractedFailureService;

    @KafkaListener(topics = KafkaConstants.TOPIC_METADATA_FAILURE, groupId = "database-restoration-service-group")
    void onExtractionFailure(MetadataExtractedFailureEvent event, Acknowledgment ack) {
        log.info("-----> Started compensating transaction {}", event);
        metadataExtractedFailureService.handle(event);
        log.info("<----- Finished compensating transaction {}", event);
        ack.acknowledge();
    }
}
