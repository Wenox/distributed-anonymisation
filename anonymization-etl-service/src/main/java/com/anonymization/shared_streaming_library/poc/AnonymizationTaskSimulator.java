package com.anonymization.shared_streaming_library.poc;

import com.anonymization.shared_streaming_library.AnonymizationTask;
import com.anonymization.shared_streaming_library.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AnonymizationTaskSimulator {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate2;

    public void sendAnonymizationTasks(int numberOfTasks, String topic) {
        for (int i = 0; i < numberOfTasks; i++) {
            AnonymizationTask anonymizationTask = new AnonymizationTask();
            anonymizationTask.setTaskId("anonymizationTaskId" + i);
            anonymizationTask.setType(OperationType.SUPPRESSION);
            anonymizationTask.setTableName("employees");
            anonymizationTask.setColumnName("salary");
            anonymizationTask.setColumnType("0");
            anonymizationTask.setWorksheetId(UUID.randomUUID().toString());

            log.info("Sending anonymization task {} to topic {}!", anonymizationTask, topic);
            kafkaTemplate2.send(topic, anonymizationTask);
        }
    }
}
