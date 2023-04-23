package com.anonymization.shared_streaming_library.poc;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private String bootstrapAddress = "localhost:9093";

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "anonymization-group");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configProps;
    }

    @Bean
    public ConsumerFactory<String, AnimalTask> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
                new JsonDeserializer<>(AnimalTask.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AnimalTask> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AnimalTask> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

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
