package com.wenox.anonymization.shared_events_library.config;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

@Slf4j
@Configuration
public class KafkaDeadLetterConfig {

    @Bean
    public DeadLetterPublishingRecoverer loggingDeadLetterPublishingRecoverer(KafkaTemplate<?, ?> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (consumerRecord, exception) -> new TopicPartition(KafkaConstants.TOPIC_DEAD_LETTER, consumerRecord.partition())) {
            @Override
            protected void publish(ProducerRecord<Object, Object> outRecord, KafkaOperations<Object, Object> kafkaTemplate, ConsumerRecord<?, ?> inRecord) {
                log.warn("Publishing to dead letter - Source Topic: {}, Partition: {}, Offset: {}, Exception: {}", inRecord.topic(), inRecord.partition(), inRecord.offset(), "Exception details");
                super.publish(outRecord, kafkaTemplate, inRecord);
            }
        };
    }
}
