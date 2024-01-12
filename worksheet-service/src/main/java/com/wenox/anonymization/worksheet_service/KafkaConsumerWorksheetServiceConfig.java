package com.wenox.anonymization.worksheet_service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerWorksheetServiceConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.consumer.groupId}")
    private String groupId;

    @Value(value = "${kafka.consumer.deadletter.enabled:true}")
    private boolean deadletterEnabled;

    @Value(value = "${kafka.consumer.at-least-once-delivery.enabled:true}")
    private boolean atLeastOnceDeliveryEnabled;

    @Value(value = "${kafka.consumer.max-poll-interval-ms:30000}")
    private int maxPollIntervalMsConfig;

    @Bean
    public ConsumerFactory<String, String> worksheetConsumerFactory() {
        log.info("Creating consumer factory");
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMsConfig);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, !atLeastOnceDeliveryEnabled);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> worksheetKafkaListenerContainerFactory(DeadLetterPublishingRecoverer loggingDeadLetterPublishingRecoverer,
                                                                                                          BackOff backoff) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(worksheetConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        if (deadletterEnabled) {
            factory.setCommonErrorHandler(new DefaultErrorHandler(loggingDeadLetterPublishingRecoverer, backoff));
        } else {
            log.warn("Kafka Consumers will not use Dead Letter.");
            factory.setCommonErrorHandler(new DefaultErrorHandler(backoff));
        }

        return factory;
    }
}