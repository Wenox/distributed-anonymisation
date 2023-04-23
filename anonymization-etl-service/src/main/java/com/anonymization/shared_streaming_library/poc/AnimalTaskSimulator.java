package com.anonymization.shared_streaming_library.poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import com.wenox.anonymization.shared_events_library.impl.LoggingKafkaTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnimalTaskSimulator {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate2;

    public void sendAnimalTasks(int numberOfTasks, String topic) throws JsonProcessingException {
        for (int i = 0; i < numberOfTasks; i++) {
            AnimalTask animalTask = new AnimalTask();
            animalTask.setAnimalId("animalId" + i);
            animalTask.setAnimalName("animalName" + i);
            animalTask.setAge(i);

            log.info("Sending animal {} to topic!", animalTask);
            kafkaTemplate2.send(topic, animalTask);
        }
    }
}
