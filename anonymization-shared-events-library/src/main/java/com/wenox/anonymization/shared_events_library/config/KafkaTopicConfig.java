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
    public NewTopic topic1() {
        log.warn("Creating topic: {}", KafkaConstants.TOPIC_BLUEPRINTS);
        return TopicBuilder.name(KafkaConstants.TOPIC_BLUEPRINTS).build();
    }
}
