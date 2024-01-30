package com.wenox.anonymization.worksheet_service.task;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.worksheet_service.exception.TaskNotFoundException;
import com.wenox.anonymization.worksheet_service.operation.Operation;
import com.wenox.anonymization.worksheet_service.operation.OperationRepository;
import com.wenox.anonymization.worksheet_service.operation.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskStatusUpdater {

    private final OperationRepository repository;
    private final Map<TaskStatus, StatusUpdateStrategy> updateStrategies;

    @Value("${min.delay.ms:}")
    private Integer minDelayMs;

    @Value("${max.delay.ms}")
    private Integer maxDelayMs;

    private void induceRandomDelay() {
        try {
            int delay = getRandomDelay(minDelayMs, maxDelayMs);
            log.info("Delaying execution for {} ms...", delay);
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private int getRandomDelay(int minDelay, int maxDelay) {
        if (minDelay >= maxDelay) {
            throw new IllegalArgumentException("Max delay must be greater than min delay");
        }
        return new Random().nextInt(maxDelay - minDelay + 1) + minDelay;
    }

    public TaskStatusUpdater(OperationRepository repository, List<StatusUpdateStrategy> updateStrategies) {
        this.repository = repository;
        this.updateStrategies = updateStrategies
                .stream()
                .collect(Collectors.toMap(StatusUpdateStrategy::getApplicableStatus, Function.identity()));
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_EXTRACTION_SUCCESS, groupId = "worksheet-service-group", containerFactory = "worksheetKafkaListenerContainerFactory")
    void onExtracted(String taskId, Acknowledgment ack) {
        updateStatus(taskId, TaskStatus.EXTRACTED_COLUMN_TUPLE);
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_TRANSFORMATION_ANONYMIZE_SUCCESS, groupId = "worksheet-service-group", containerFactory = "worksheetKafkaListenerContainerFactory")
    void onTransformedAnonymization(String taskId, Acknowledgment ack) {
        updateStatus(taskId, TaskStatus.APPLIED_ANONYMISATION);
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_TRANSFORMATION_SCRIPT_SUCCESS, groupId = "worksheet-service-group", containerFactory = "worksheetKafkaListenerContainerFactory")
    void onTransformedSqlScript(String taskId, Acknowledgment ack) {
        updateStatus(taskId, TaskStatus.CREATED_FRAGMENT);
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_LOAD_SUCCESS, groupId = "worksheet-service-group", containerFactory = "worksheetKafkaListenerContainerFactory")
    void onLoaded(String taskId, Acknowledgment ack) {
        updateStatus(taskId, TaskStatus.LOADED_FRAGMENT);
        ack.acknowledge();
    }

    private void updateStatus(String taskId, TaskStatus newStatus) {
        induceRandomDelay();
        Operation operation = repository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with taskId: " + taskId));

        StatusUpdateStrategy strategy = updateStrategies.get(newStatus);
        if (strategy.canUpdateStatus(operation)) {
            operation.setStatus(newStatus);
            repository.save(operation);
            log.info("Updated task from {} to {} – taskId: {}", operation.getStatus(), newStatus, taskId);
        } else {
            log.info("Ignoring task update to {} – already updated to {} – taskId: {}", newStatus, operation.getStatus(), taskId);
        }
    }
}

interface StatusUpdateStrategy {
    boolean canUpdateStatus(Operation operation);
    TaskStatus getApplicableStatus();
}

@Component
class ExtractedStatusUpdateStrategy implements StatusUpdateStrategy {
    @Override
    public boolean canUpdateStatus(Operation operation) {
        return operation.getStatus() == TaskStatus.INITIALISED;
    }

    @Override
    public TaskStatus getApplicableStatus() {
        return TaskStatus.EXTRACTED_COLUMN_TUPLE;
    }
}

@Component
class TransformedAnonymizationStatusUpdateStrategy implements StatusUpdateStrategy {
    private final Set<TaskStatus> postTransformedAnonymization = Set.of(TaskStatus.APPLIED_ANONYMISATION, TaskStatus.CREATED_FRAGMENT, TaskStatus.LOADED_FRAGMENT);

    @Override
    public boolean canUpdateStatus(Operation operation) {
        return !postTransformedAnonymization.contains(operation.getStatus());
    }

    @Override
    public TaskStatus getApplicableStatus() {
        return TaskStatus.APPLIED_ANONYMISATION;
    }
}

@Component
class TransformedSqlScriptStatusUpdateStrategy implements StatusUpdateStrategy {
    private final Set<TaskStatus> postTransformedSqlScript = Set.of(TaskStatus.CREATED_FRAGMENT, TaskStatus.LOADED_FRAGMENT);

    @Override
    public boolean canUpdateStatus(Operation operation) {
        return !postTransformedSqlScript.contains(operation.getStatus());
    }

    @Override
    public TaskStatus getApplicableStatus() {
        return TaskStatus.CREATED_FRAGMENT;
    }
}

@Component
class FinishedStatusUpdateStrategy implements StatusUpdateStrategy {
    @Override
    public boolean canUpdateStatus(Operation operation) {
        return operation.getStatus() != TaskStatus.LOADED_FRAGMENT;
    }

    @Override
    public TaskStatus getApplicableStatus() {
        return TaskStatus.LOADED_FRAGMENT;
    }
}

