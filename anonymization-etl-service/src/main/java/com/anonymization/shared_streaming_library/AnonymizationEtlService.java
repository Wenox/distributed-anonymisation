package com.anonymization.shared_streaming_library;

import com.anonymization.shared_streaming_library.poc.AnonymizationTaskSimulator;
import com.anonymization.shared_streaming_library.poc.StreamingService2;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableScheduling
public class AnonymizationEtlService {

    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        ConfigurableApplicationContext context = SpringApplication.run(AnonymizationEtlService.class, args);

        // Send multiple AnimalTask messages to the Kafka topic
        AnonymizationTaskSimulator anonymizationTaskSimulator = context.getBean(AnonymizationTaskSimulator.class);
        anonymizationTaskSimulator.sendAnonymizationTasks(10, KafkaConstants.TOPIC_OPERATIONS);


        StreamingService2 anonymizationService = context.getBean(StreamingService2.class);
        anonymizationService.processAnonymizationTasks();
    }
}
