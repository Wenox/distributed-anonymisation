package com.anonymization.etl.config.tests;

import com.anonymization.etl.domain.OperationType;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RestController
@RequestMapping("/api/simulator")
public class AnonymizationTaskSimulator implements Serializable {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate2;

    @PostConstruct
    public void init() {
        this.sendAnonymizationTasks("644847fce7e330525a716748", 0, KafkaConstants.TOPIC_OPERATIONS);
    }

    @PostMapping("/start")
    public void send(@RequestParam("blueprint_id") String blueprintId, @RequestParam("number_of_tasks") Integer numberOfTasks) {
        sendAnonymizationTasks(blueprintId, numberOfTasks, KafkaConstants.TOPIC_OPERATIONS);
    }

    @Scheduled(fixedDelayString = "20000")
    public void trigggggger() {
        log.info("TRIGGGGGGGGERING");
//        sendAnonymizationTasks("644847fce7e330525a716748", 50, KafkaConstants.TOPIC_OPERATIONS);
    }

    public void sendAnonymizationTasks(String blueprintId, int numberOfTasks, String topic) {
        var now = LocalDateTime.now();
        for (int i = 0; i < numberOfTasks; i++) {
            AnonymizationTask suppressionTask = new AnonymizationTask();
            suppressionTask.setTaskId("suppressionTask-" + i);
            suppressionTask.setType(OperationType.SUPPRESSION);
            suppressionTask.setTableName("employees");
            suppressionTask.setColumnName("salary");
            suppressionTask.setColumnType("0");
            suppressionTask.setWorksheetId("worksheetId-" + now);
            suppressionTask.setConfiguration(Map.of("token", "***"));
            suppressionTask.setBlueprintId(blueprintId);

            log.info("Sending suppression task {} to topic {}!", suppressionTask, topic);
            kafkaTemplate2.send(topic, suppressionTask);
        }

        for (int i = 0; i < numberOfTasks; i++) {
            AnonymizationTask shuffleTask = new AnonymizationTask();
            shuffleTask.setTaskId("shuffleTask-" + i);
            shuffleTask.setType(OperationType.SHUFFLE);
            shuffleTask.setTableName("employees");
            shuffleTask.setColumnName("salary");
            shuffleTask.setColumnType("0");
            shuffleTask.setWorksheetId("worksheetId-" + now);
            shuffleTask.setConfiguration(Map.of("repetitions", "false"));
            shuffleTask.setBlueprintId(blueprintId);

            log.info("Sending shuffle task {} to topic {}!", shuffleTask, topic);
            kafkaTemplate2.send(topic, shuffleTask);
        }
    }
}
