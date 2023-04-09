package com.wenox.anonymization.shared_events_library.impl;

import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingKafkaTemplate<K, V> implements KafkaTemplateWrapper<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    @Override
    public void send(String topic, V value) {
        log.info("Publishing to Kafka | Topic: {} | Value: {}", topic, value);
        kafkaTemplate.send(topic, value);
    }

    @Override
    public void send(String topic, K key, V value) {
        log.info("Publishing to Kafka | Topic: {} | Key: {} | Value: {}", topic, key, value);
        kafkaTemplate.send(topic, key, value);
    }
}
