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
class MessagePublisherKafkaAdapter implements MessagePublisher {

    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;

    @Override
    public void send(DatabaseRestoredSuccessEvent event) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_SUCCESS, event);
    }

    @Override
    public void send(DatabaseRestoredFailureEvent event) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_FAILURE, event);
    }

    @Override
    public void send(MetadataExtractedFailureEvent event) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_RESTORE_FAILURE, event);
    }
}
