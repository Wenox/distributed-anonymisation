package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import com.wenox.anonymization.worksheet_service.domain.*;
import com.wenox.anonymization.worksheet_service.exception.InactiveRestorationException;
import com.wenox.anonymization.worksheet_service.exception.WorksheetNotFoundException;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultWorksheetService implements WorksheetService {

    private final WorksheetRepository worksheetRepository;
    private final WorksheetCreatedEventRepository worksheetCreatedEventRepository;
    private final WorksheetMapper worksheetMapper;
    private final DependenciesService dependenciesService;

    public Either<FailureResponse, CreateWorksheetResponse> createWorksheet(CreateWorksheetRequest request) {
        Either<FailureResponse, CreateWorksheetResponse> eitherResponse = dependenciesService.retrieveDependencies(request);

        return eitherResponse.flatMap(response -> {

            if (!response.getRestoration().isActive()) {
                throw new InactiveRestorationException("Inactive restoration " + response.getRestoration());
            }

            String worksheetId = UUID.randomUUID().toString();
            Worksheet worksheet = worksheetMapper.toWorksheet(worksheetId, request, response);
            WorksheetCreatedEvent worksheetCreatedEvent = worksheetMapper.toWorksheetCreatedEvent(worksheetId, request.blueprintId(), worksheet.getRestoreMode());

            worksheetRepository.save(worksheet);
            worksheetCreatedEventRepository.save(worksheetCreatedEvent);

            response.setWorksheet(worksheet);
            return Either.right(response);
        });
    }

    public WorksheetResponse getWorksheet(String worksheetId) {
        return worksheetRepository.findById(worksheetId)
                .map(worksheetMapper::toResponse)
                .orElseThrow(() -> new WorksheetNotFoundException("Worksheet not found with worksheetId: " + worksheetId));
    }
}
