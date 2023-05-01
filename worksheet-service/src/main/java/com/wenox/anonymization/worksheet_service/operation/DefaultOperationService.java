package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import com.wenox.anonymization.worksheet_service.FailureResponse;
import com.wenox.anonymization.worksheet_service.TaskStatusResponse;
import com.wenox.anonymization.worksheet_service.WorksheetRepository;
import com.wenox.anonymization.worksheet_service.domain.*;
import com.wenox.anonymization.worksheet_service.exception.WorksheetNotFoundException;
import com.wenox.anonymization.worksheet_service.operation.base.AddOperationRequest;
import com.wenox.anonymization.worksheet_service.task.AnonymizationTask;
import com.wenox.anonymization.worksheet_service.task.AnonymizationTaskMapper;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.cassandra.core.CassandraBatchOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultOperationService implements OperationService {

    private final OperationRepository operationRepository;
    private final WorksheetRepository worksheetRepository;
    private final OperationMapper<AddOperationRequest> operationMapper;
    private final AnonymizationTaskMapper anonymizationTaskMapper;
    private final KafkaTemplateWrapper<String, Object> kafkaTemplateWrapper;

    @Override
    public List<TaskStatusResponse> getTasksStatuses(String worksheetId) {
        List<Operation> tasks = operationRepository.findByWorksheetId(worksheetId);
        return tasks.stream().map(TaskStatusResponse::from).toList();
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

        AnonymizationTask task = anonymizationTaskMapper.toAnonymizationTask(operation, worksheet);
        kafkaTemplateWrapper.send(KafkaConstants.TOPIC_OPERATIONS, task);

        return Either.right(operationMapper.toResponse(operation, worksheet));
    }
}
