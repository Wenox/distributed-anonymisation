package com.wenox.anonymization.shared_events_library.api;

public interface KafkaTemplateWrapper<K, V> {

    void send(String topic, V value);

    void send(String topic, K key, V value);
}
