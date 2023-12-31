package com.wenox.anonymization.blueprint_service.config;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "blueprint-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<Object> deserializer = new JsonDeserializer<>(Object.class);
        deserializer.addTrustedPackages("com.wenox.anonymization.shared_events_library");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public DeadLetterPublishingRecoverer loggingDeadLetterPublishingRecoverer(KafkaTemplate<?, ?> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (consumerRecord, exception) -> new TopicPartition(KafkaConstants.TOPIC_DEAD_LETTER, consumerRecord.partition())) {
            @Override
            protected void publish(ProducerRecord<Object, Object> outRecord, KafkaOperations<Object, Object> kafkaTemplate, ConsumerRecord<?, ?> inRecord) {
                log.info("Publishing to dead letter - Source Topic: {}, Partition: {}, Offset: {}, Exception: {}", inRecord.topic(), inRecord.partition(), inRecord.offset(), "Exception details");
                super.publish(outRecord, kafkaTemplate, inRecord);
            }
        };
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(DeadLetterPublishingRecoverer loggingDeadLetterPublishingRecoverer) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                loggingDeadLetterPublishingRecoverer,
                new FixedBackOff(1000L, 3L)
        );
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
