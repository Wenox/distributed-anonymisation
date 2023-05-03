package com.wenox.anonymization.shared_events_library.config;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
@PropertySource("classpath:events-library.properties")
public class KafkaTopicConfig {

    @Bean
    public NewTopic topicBlueprintCreated() {
        return createNewTopic(KafkaConstants.TOPIC_CREATED_BLUEPRINT);
    }

    @Bean
    public NewTopic topicAnonymizationExecution() {
        return createNewTopic(KafkaConstants.TOPIC_ANONYMIZATION_EXECUTION);
    }

    @Bean
    public NewTopic topicAnonymizationExecutionSuccess() {
        return createNewTopic(KafkaConstants.TOPIC_ANONYMIZATION_EXECUTION_SUCCESS);
    }

    @Bean
    public NewTopic topicAnonymizationExecutionFailure() {
        return createNewTopic(KafkaConstants.TOPIC_ANONYMIZATION_EXECUTION_FAILURE);
    }

    @Bean
    public NewTopic topicDatabaseRestoredSuccess() {
        return createNewTopic(KafkaConstants.TOPIC_RESTORE_SUCCESS);
    }

    @Bean
    public NewTopic topicDatabaseRestoredFailure() {
        return createNewTopic(KafkaConstants.TOPIC_RESTORE_FAILURE);
    }

    @Bean
    public NewTopic topicOperations() {
        return createNewTopic(KafkaConstants.TOPIC_OPERATIONS);
    }

    @Bean
    public NewTopic topicLoadSuccess() {
        return createNewTopic(KafkaConstants.TOPIC_LOAD_SUCCESS);
    }

    @Bean
    public NewTopic topicLoadFailure() {
        return createNewTopic(KafkaConstants.TOPIC_LOAD_FAILURE);
    }

    @Bean
    public NewTopic topicExtractionSuccess() {
        return createNewTopic(KafkaConstants.TOPIC_EXTRACTION_SUCCESS);
    }

    @Bean
    public NewTopic topicExtractionFailure() {
        return createNewTopic(KafkaConstants.TOPIC_EXTRACTION_FAILURE);
    }

    @Bean
    public NewTopic topicTransformationAnonymizeSuccess() {
        return createNewTopic(KafkaConstants.TOPIC_TRANSFORMATION_ANONYMIZE_SUCCESS);
    }

    @Bean
    public NewTopic topicTransformationAnonymizeFailure() {
        return createNewTopic(KafkaConstants.TOPIC_TRANSFORMATION_ANONYMIZE_FAILURE);
    }

    @Bean
    public NewTopic topicTransformationScriptSuccess() {
        return createNewTopic(KafkaConstants.TOPIC_TRANSFORMATION_SCRIPT_SUCCESS);
    }

    @Bean
    public NewTopic topicTransformationScriptFailure() {
        return createNewTopic(KafkaConstants.TOPIC_TRANSFORMATION_SCRIPT_FAILURE);
    }

    @Bean
    public NewTopic topicCreatedWorksheet() {
        return createNewTopic(KafkaConstants.TOPIC_CREATED_WORKSHEET);
    }

    private NewTopic createNewTopic(String topicName) {
        log.warn("Creating topic: {}", topicName);
        return TopicBuilder.name(topicName).build();
    }
}
