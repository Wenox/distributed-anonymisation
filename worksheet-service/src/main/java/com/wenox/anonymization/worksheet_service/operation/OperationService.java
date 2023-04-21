package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.FailureResponse;
import com.wenox.anonymization.worksheet_service.operation.base.AddOperationRequest;
import io.vavr.control.Either;

public interface OperationService {

    <T extends AddOperationRequest> Either<FailureResponse, AddOperationResponse> addOperation(String worksheetId, T request, OperationType operationType);

//    Either<FailureResponse, AddOperationResponse> addSuppression(String worksheetId, AddSuppressionRequest request);

//    Either<FailureResponse, AddOperationResponse> addShuffle(String worksheetId, AddShuffleRequest request);
}
