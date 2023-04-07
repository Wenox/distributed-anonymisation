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
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_BLUEPRINT_CREATED);
        return TopicBuilder.name(KafkaConstants.TOPIC_BLUEPRINT_CREATED).build();
    }

    @Bean
    public NewTopic topicDatabaseRestoredSuccess() {
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_DATABASE_RESTORED_SUCCESS);
        return TopicBuilder.name(KafkaConstants.TOPIC_DATABASE_RESTORED_SUCCESS).build();
    }

    @Bean
    public NewTopic topicDatabaseRestoredFailure() {
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_DATABASE_RESTORED_FAILURE);
        return TopicBuilder.name(KafkaConstants.TOPIC_DATABASE_RESTORED_FAILURE).build();
    }
}
