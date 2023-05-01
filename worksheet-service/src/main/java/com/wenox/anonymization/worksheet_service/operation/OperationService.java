package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.FailureResponse;
import com.wenox.anonymization.worksheet_service.TaskStatusResponse;
import com.wenox.anonymization.worksheet_service.operation.base.AddOperationRequest;
import io.vavr.control.Either;

import java.util.List;

public interface OperationService {

    <T extends AddOperationRequest> void asyncAddOperation(String worksheetId, T request, OperationType operationType);
    <T extends AddOperationRequest> Either<FailureResponse, AddOperationResponse> addOperation(String worksheetId, T request, OperationType operationType);
    List<TaskStatusResponse> getTasksStatuses(String worksheetId);
}
