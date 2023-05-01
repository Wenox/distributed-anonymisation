package com.wenox.anonymization.worksheet_service.task;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.worksheet_service.exception.TaskNotFoundException;
import com.wenox.anonymization.worksheet_service.operation.Operation;
import com.wenox.anonymization.worksheet_service.operation.OperationRepository;
import com.wenox.anonymization.worksheet_service.operation.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskStatusUpdater {

    private final OperationRepository operationRepository;

    @KafkaListener(topics = KafkaConstants.TOPIC_EXTRACTION_SUCCESS, groupId = "blueprint-service-group")
    public void onTaskExtracted(String taskId) {
        Operation operation = operationRepository.findById(Operation.Key.from(taskId))
                .orElseThrow(() -> new TaskNotFoundException("Task not found with taskId: " + taskId));

        if (operation.getStatus() != TaskStatus.CREATED) {
            log.info("Ignoring task update to EXTRACTED – already updated to {} – taskId: {}", operation.getStatus(), taskId);
            return;
        }

        operation.setStatus(TaskStatus.EXTRACTED);
        operationRepository.save(operation);

        log.info("Updated task from {} to EXTRACTED – taskId: {}", operation.getStatus(), taskId);
    }
}
