package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import com.wenox.anonymization.worksheet_service.FailureResponse;
import com.wenox.anonymization.worksheet_service.TasksInWorksheetResponse;
import com.wenox.anonymization.worksheet_service.WorksheetRepository;
import com.wenox.anonymization.worksheet_service.domain.*;
import com.wenox.anonymization.worksheet_service.exception.WorksheetNotFoundException;
import com.wenox.anonymization.worksheet_service.operation.base.AddOperationRequest;
import com.wenox.anonymization.worksheet_service.task.Task;
import com.wenox.anonymization.worksheet_service.task.TaskMapper;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultOperationService implements OperationService {

    private final OperationRepository operationRepository;
    private final WorksheetRepository worksheetRepository;
    private final OperationMapper<AddOperationRequest> operationMapper;
    private final TaskMapper taskMapper;
    private final KafkaTemplateWrapper<String, Object> kafkaTemplateWrapper;

    @Override
    public TasksInWorksheetResponse getTasksInWorksheetGroupedByStatus(String worksheetId) {
        List<Operation> operations = operationRepository.findByWorksheetId(worksheetId);

        Map<TaskStatus, List<String>> tasksByStatus = Arrays.stream(TaskStatus.values())
                .collect(Collectors.toMap(status -> status, status -> new ArrayList<>(), (a, b) -> a, () -> new EnumMap<>(TaskStatus.class)));

        AtomicInteger numberOfTasks = new AtomicInteger();
        boolean allSuccessful = operations.stream()
                .peek(operation -> {
                    tasksByStatus.get(operation.getStatus()).add(operation.getKey().getTaskId());
                    numberOfTasks.incrementAndGet();
                })
                .allMatch(operation -> operation.getStatus() == TaskStatus.LOADED_FRAGMENT);

        return TasksInWorksheetResponse.builder()
                .tasksByStatus(tasksByStatus)
                .allSuccessful(allSuccessful)
                .numberOfTasks(numberOfTasks.get())
                .build();
    }


    @Async
    @Override
    public <T extends AddOperationRequest> void asyncAddOperation(String worksheetId, T request, OperationType operationType) {
        addOperation(worksheetId, request, operationType);
    }

    @Override
    public <T extends AddOperationRequest> Either<FailureResponse, AddOperationResponse> addOperation(String worksheetId, T request, OperationType operationType) {
        Worksheet worksheet = worksheetRepository.findById(worksheetId)
                .orElseThrow(() -> new WorksheetNotFoundException("Worksheet not found with worksheetId: " + worksheetId));

        if (worksheet.getWorksheetStatus() == WorksheetStatus.CLOSED) {
            return Either.left(FailureResponse.toWorksheetClosed(worksheetId));
        }

        Optional<Table> tableOptional = Optional.ofNullable(worksheet.getMetadata())
                .map(Metadata::tables)
                .map(tables -> tables.get(request.getTable()));

        if (tableOptional.isEmpty()) {
            return Either.left(FailureResponse.toTableNotFound(worksheetId, request.getTable()));
        }

        Optional<Column> columnOptional = tableOptional
                .map(Table::getColumns)
                .map(columns -> columns.get(request.getColumn()));

        if (columnOptional.isEmpty()) {
            return Either.left(FailureResponse.toColumnNotFound(worksheetId, request.getTable(), request.getColumn()));
        }

        // possible side effect extension point with per-operation-type capabilities...
        sideEffectNotImplemented(request, operationType);

        return saveOperationAndPublishTask(worksheet, request);
    }

    private <T extends AddOperationRequest> void sideEffectNotImplemented(T request, OperationType operationType) {
        switch (operationType) {
            case SUPPRESSION, SHUFFLE, GENERALISATION:
                break;
            default:
                throw new IllegalStateException("Unsupported operation type: " + operationType);
        }
    }

    public <T extends AddOperationRequest> Either<FailureResponse, AddOperationResponse> saveOperationAndPublishTask(Worksheet worksheet, T request) {
        Operation operation = operationMapper.toOperation(worksheet, request);

        operationRepository.save(operation);

        Task task = taskMapper.toTask(operation, worksheet);
        kafkaTemplateWrapper.send(KafkaConstants.TOPIC_OPERATIONS, task);

        return Either.right(operationMapper.toResponse(operation, worksheet));
    }
}
