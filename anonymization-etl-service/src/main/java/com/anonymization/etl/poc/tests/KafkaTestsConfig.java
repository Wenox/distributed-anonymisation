package com.anonymization.etl.poc.tests;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTestsConfig {

    private String bootstrapAddress = "localhost:9093";

    // producer
    @Bean
    public Map<String, Object> producerConfigs2() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory2() {
        return new DefaultKafkaProducerFactory<>(producerConfigs2());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate2() {
        return new KafkaTemplate<>(producerFactory2());
    }
}
