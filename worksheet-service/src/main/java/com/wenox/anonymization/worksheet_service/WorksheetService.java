package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.CreateWorksheetResponse;
import io.vavr.control.Either;

public interface WorksheetService {

    Either<FailureResponse, CreateWorksheetResponse> createWorksheet(CreateWorksheetRequest request);

    WorksheetResponse getWorksheet(String worksheetId);
}
