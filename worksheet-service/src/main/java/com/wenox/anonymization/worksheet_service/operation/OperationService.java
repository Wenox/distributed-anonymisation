package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.FailureResponse;
import io.vavr.control.Either;

public interface OperationService {

    Either<FailureResponse, AddOperationResponse> addSuppression(String worksheetId, AddSuppressionRequest request);
}
