package com.anonymization.etl.poc.tests;

import com.anonymization.etl.OperationType;
import com.anonymization.etl.poc.tasks.ShuffleTask;
import com.anonymization.etl.poc.tasks.SuppressionTask;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.UUID;

@Service
@Slf4j
public class AnonymizationTaskSimulator implements Serializable {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate2;

    @PostConstruct
    public void init() {
        this.sendAnonymizationTasks(10, KafkaConstants.TOPIC_OPERATIONS);
    }

    public void sendAnonymizationTasks(int numberOfTasks, String topic) {
        for (int i = 0; i < numberOfTasks; i++) {
            SuppressionTask suppressionTask = new SuppressionTask();
            suppressionTask.setTaskId("suppressionTask-" + i);
            suppressionTask.setType(OperationType.SUPPRESSION);
            suppressionTask.setTableName("employees");
            suppressionTask.setColumnName("salary");
            suppressionTask.setColumnType("0");
            suppressionTask.setWorksheetId(UUID.randomUUID().toString());
            suppressionTask.setToken("***");

            log.info("Sending suppression task {} to topic {}!", suppressionTask, topic);
            kafkaTemplate2.send(topic, suppressionTask);
        }

        for (int i = 0; i < numberOfTasks; i++) {
            ShuffleTask shuffleTask = new ShuffleTask();
            shuffleTask.setTaskId("shuffleTask-" + i);
            shuffleTask.setType(OperationType.SHUFFLE);
            shuffleTask.setTableName("employees");
            shuffleTask.setColumnName("salary");
            shuffleTask.setColumnType("0");
            shuffleTask.setWorksheetId(UUID.randomUUID().toString());
            shuffleTask.setRepetitions(false);

            log.info("Sending shuffle task {} to topic {}!", shuffleTask, topic);
            kafkaTemplate2.send(topic, shuffleTask);
        }
    }
}
