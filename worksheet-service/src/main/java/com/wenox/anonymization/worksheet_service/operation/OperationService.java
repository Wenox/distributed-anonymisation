package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.FailureResponse;
import com.wenox.anonymization.worksheet_service.operation.base.AddOperationRequest;
import io.vavr.control.Either;

import java.util.List;
import java.util.Map;

public interface OperationService {

    <T extends AddOperationRequest> void asyncAddOperation(String worksheetId, T request, OperationType operationType);
    <T extends AddOperationRequest> Either<FailureResponse, AddOperationResponse> addOperation(String worksheetId, T request, OperationType operationType);
    Map<TaskStatus, List<String>> getTasksInWorksheetGroupedByStatus(String worksheetId);
}
