package com.wenox.anonymization.worksheet_service.task;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.worksheet_service.exception.TaskNotFoundException;
import com.wenox.anonymization.worksheet_service.operation.Operation;
import com.wenox.anonymization.worksheet_service.operation.OperationRepository;
import com.wenox.anonymization.worksheet_service.operation.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskStatusUpdater {

    private final OperationRepository repository;
    private final Map<TaskStatus, StatusUpdateStrategy> updateStrategies;

    public TaskStatusUpdater(OperationRepository repository, List<StatusUpdateStrategy> updateStrategies) {
        this.repository = repository;
        this.updateStrategies = updateStrategies
                .stream()
                .collect(Collectors.toMap(StatusUpdateStrategy::getApplicableStatus, Function.identity()));
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_EXTRACTION_SUCCESS, groupId = "blueprint-service-group")
    void onExtracted(String taskId) {
        updateStatus(taskId, TaskStatus.EXTRACTED);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_TRANSFORMATION_ANONYMIZE_SUCCESS, groupId = "blueprint-service-group")
    void onTransformedAnonymization(String taskId) {
        updateStatus(taskId, TaskStatus.TRANSFORMED_ANONYMIZATION);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_TRANSFORMATION_SCRIPT_SUCCESS, groupId = "blueprint-service-group")
    void onTransformedSqlScript(String taskId) {
        updateStatus(taskId, TaskStatus.TRANSFORMED_SQL_SCRIPT);
    }

    @KafkaListener(topics = KafkaConstants.TOPIC_LOAD_SUCCESS, groupId = "blueprint-service-group")
    void onLoaded(String taskId) {
        updateStatus(taskId, TaskStatus.FINISHED);
    }

    private void updateStatus(String taskId, TaskStatus newStatus) {
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
        return operation.getStatus() == TaskStatus.STARTED;
    }

    @Override
    public TaskStatus getApplicableStatus() {
        return TaskStatus.EXTRACTED;
    }
}

@Component
class TransformedAnonymizationStatusUpdateStrategy implements StatusUpdateStrategy {
    private final Set<TaskStatus> postTransformedAnonymization = Set.of(TaskStatus.TRANSFORMED_ANONYMIZATION, TaskStatus.TRANSFORMED_SQL_SCRIPT, TaskStatus.FINISHED);

    @Override
    public boolean canUpdateStatus(Operation operation) {
        return !postTransformedAnonymization.contains(operation.getStatus());
    }

    @Override
    public TaskStatus getApplicableStatus() {
        return TaskStatus.TRANSFORMED_ANONYMIZATION;
    }
}

@Component
class TransformedSqlScriptStatusUpdateStrategy implements StatusUpdateStrategy {
    private final Set<TaskStatus> postTransformedSqlScript = Set.of(TaskStatus.TRANSFORMED_SQL_SCRIPT, TaskStatus.FINISHED);

    @Override
    public boolean canUpdateStatus(Operation operation) {
        return !postTransformedSqlScript.contains(operation.getStatus());
    }

    @Override
    public TaskStatus getApplicableStatus() {
        return TaskStatus.TRANSFORMED_SQL_SCRIPT;
    }
}

@Component
class FinishedStatusUpdateStrategy implements StatusUpdateStrategy {
    @Override
    public boolean canUpdateStatus(Operation operation) {
        return operation.getStatus() != TaskStatus.FINISHED;
    }

    @Override
    public TaskStatus getApplicableStatus() {
        return TaskStatus.FINISHED;
    }
}

