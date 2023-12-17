package com.wenox.anonymization.metadata_extraction_service.adapters.outbound.messaging;

import com.wenox.anonymization.metadata_extraction_service.domain.port.MessagePublisher;
import com.wenox.anonymization.shared_events_library.MetadataExtractedFailureEvent;
import com.wenox.anonymization.shared_events_library.MetadataExtractedSuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessagePublisherKafkaAdapter implements MessagePublisher {

    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;

    public void sendMetadataExtractedSuccess(MetadataExtractedSuccessEvent event) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_METADATA_SUCCESS, event);
    }

    public void sendMetadataExtractedFailure(MetadataExtractedFailureEvent event) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_METADATA_FAILURE, event);
    }
}