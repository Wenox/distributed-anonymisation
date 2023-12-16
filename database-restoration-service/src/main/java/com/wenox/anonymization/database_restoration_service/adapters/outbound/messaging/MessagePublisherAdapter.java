package com.wenox.anonymization.database_restoration_service.adapters.outbound.messaging;

import com.wenox.anonymization.database_restoration_service.domain.ports.MessagePublisher;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MessagePublisherAdapter implements MessagePublisher {

    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;

    @Override
    public void sendDatabaseRestoredSuccess(String blueprintId, String databaseName) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_SUCCESS, new DatabaseRestoredSuccessEvent(blueprintId, databaseName));
    }

    @Override
    public void sendDatabaseRestoredFailure(String blueprintId, String databaseName, String errorMessage, Exception ex) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_FAILURE, new DatabaseRestoredFailureEvent(blueprintId, databaseName, errorMessage, ex));
    }

    @Override
    public void sendMetadataExtractedFailure(MetadataExtractedFailureEvent event) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_FAILURE, event);
    }
}
