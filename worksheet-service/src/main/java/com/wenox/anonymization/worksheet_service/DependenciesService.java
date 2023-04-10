package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.CreateWorksheetResponse;
import io.vavr.control.Either;

public interface DependenciesService {

    Either<FailureResponse, CreateWorksheetResponse> retrieveDependencies(CreateWorksheetRequest dto);
}
