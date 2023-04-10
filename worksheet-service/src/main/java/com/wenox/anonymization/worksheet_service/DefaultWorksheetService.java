package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.*;
import com.wenox.anonymization.worksheet_service.exception.InactiveRestorationException;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultWorksheetService implements WorksheetService {

    private final WorksheetRepository worksheetRepository;
    private final WorksheetMapper worksheetMapper;
    private final DependenciesService dependenciesService;

    public Either<FailureResponse, CreateWorksheetResponse> createWorksheet(CreateWorksheetRequest request) {
        Either<FailureResponse, CreateWorksheetResponse> eitherResponse = dependenciesService.retrieveDependencies(request);

        return eitherResponse.flatMap(response -> {

            if (!response.restoration().isActive()) {
                throw new InactiveRestorationException("Inactive restoration " + response.restoration());
            }

            worksheetRepository.save(worksheetMapper.toWorksheet(request, response));

            return Either.right(response);
        });
    }
}
