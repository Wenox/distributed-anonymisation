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

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskStatusUpdater {

    private final OperationRepository operationRepository;

    private final Set<TaskStatus> statusesPostTransformedAnonymization = Set.of(TaskStatus.TRANSFORMED_ANONYMIZATION, TaskStatus.TRANSFORMED_SQL_SCRIPT, TaskStatus.FINISHED);
    private final Set<TaskStatus> statusesPostTransformedSqlScript = Set.of(TaskStatus.TRANSFORMED_SQL_SCRIPT, TaskStatus.FINISHED);

    @KafkaListener(topics = KafkaConstants.TOPIC_EXTRACTION_SUCCESS, groupId = "blueprint-service-group")
    public void onExtracted(String taskId) {
        Operation operation = operationRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with taskId: " + taskId));

        TaskStatus existingStatus = operation.getStatus();
        if (existingStatus != TaskStatus.STARTED) {
            log.info("Ignoring task update to {} – already updated to {} – taskId: {}", TaskStatus.EXTRACTED, existingStatus, taskId);
            return;
        }

        operation.setStatus(TaskStatus.EXTRACTED);
        operationRepository.save(operation);

        log.info("Updated task from {} to {} – taskId: {}", existingStatus, TaskStatus.EXTRACTED, taskId);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_TRANSFORMATION_ANONYMIZE_SUCCESS, groupId = "blueprint-service-group")
    public void onTransformedAnonymization(String taskId) {
        Operation operation = operationRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with taskId: " + taskId));

        TaskStatus existingStatus = operation.getStatus();
        if (statusesPostTransformedAnonymization.contains(existingStatus)) {
            log.info("Ignoring task update to {} – already updated to {} – taskId: {}", TaskStatus.TRANSFORMED_ANONYMIZATION, existingStatus, taskId);
            return;
        }

        operation.setStatus(TaskStatus.TRANSFORMED_ANONYMIZATION);
        operationRepository.save(operation);

        log.info("Updated task from {} to {} – taskId: {}", existingStatus, TaskStatus.TRANSFORMED_ANONYMIZATION, taskId);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_TRANSFORMATION_SCRIPT_SUCCESS, groupId = "blueprint-service-group")
    public void onTransformedSqlScript(String taskId) {
        Operation operation = operationRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with taskId: " + taskId));

        TaskStatus existingStatus = operation.getStatus();
        if (statusesPostTransformedSqlScript.contains(existingStatus)) {
            log.info("Ignoring task update to {} – already updated to {} – taskId: {}", TaskStatus.TRANSFORMED_SQL_SCRIPT, existingStatus, taskId);
            return;
        }

        operation.setStatus(TaskStatus.TRANSFORMED_SQL_SCRIPT);
        operationRepository.save(operation);

        log.info("Updated task from {} to {} – taskId: {}", existingStatus, TaskStatus.TRANSFORMED_SQL_SCRIPT, taskId);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_LOAD_SUCCESS, groupId = "blueprint-service-group")
    public void onLoaded(String taskId) {
        Operation operation = operationRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with taskId: " + taskId));

        TaskStatus existingStatus = operation.getStatus();
        if (existingStatus == TaskStatus.FINISHED) {
            log.info("Ignoring task update to {} – already updated to {} – taskId: {}", TaskStatus.FINISHED, existingStatus, taskId);
            return;
        }

        operation.setStatus(TaskStatus.FINISHED);
        operationRepository.save(operation);

        log.info("Updated task from {} to {} – taskId: {}", existingStatus, TaskStatus.FINISHED, taskId);
    }
}
