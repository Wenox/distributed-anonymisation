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
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_CREATE_BLUEPRINT);
        return TopicBuilder.name(KafkaConstants.TOPIC_CREATE_BLUEPRINT).build();
    }

    @Bean
    public NewTopic topicDatabaseRestoredSuccess() {
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_RESTORE_SUCCESS);
        return TopicBuilder.name(KafkaConstants.TOPIC_RESTORE_SUCCESS).build();
    }

    @Bean
    public NewTopic topicDatabaseRestoredFailure() {
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_RESTORE_FAILURE);
        return TopicBuilder.name(KafkaConstants.TOPIC_RESTORE_FAILURE).build();
    }

    @Bean
    public NewTopic topicOperations() {
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_OPERATIONS);
        return TopicBuilder.name(KafkaConstants.TOPIC_OPERATIONS).build();
    }

    @Bean
    public NewTopic topicOperationSuccess() {
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_OPERATION_SUCCESS);
        return TopicBuilder.name(KafkaConstants.TOPIC_OPERATION_SUCCESS).build();
    }

    @Bean
    public NewTopic topicOperationFailure() {
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_OPERATION_FAILURE);
        return TopicBuilder.name(KafkaConstants.TOPIC_OPERATION_FAILURE).build();
    }
}
