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

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultWorksheetService implements WorksheetService {

    private final WorksheetRepository worksheetRepository;
    private final WorksheetMapper worksheetMapper;
    private final DependenciesService dependenciesService;
    private final KafkaTemplateWrapper<String, Object> kafkaTemplateWrapper;

    private final boolean isStrictMode = false;

    public Either<FailureResponse, CreateWorksheetResponse> createWorksheet(CreateWorksheetRequest request) {
        Either<FailureResponse, CreateWorksheetResponse> eitherResponse = dependenciesService.retrieveDependencies(request);

        return eitherResponse.flatMap(response -> {

            if (!response.getRestoration().isActive()) {
                throw new InactiveRestorationException("Inactive restoration " + response.getRestoration());
            }

            Worksheet worksheet = worksheetRepository.save(worksheetMapper.toWorksheet(request, response));
            response.setWorksheet(worksheet);
            kafkaTemplateWrapper.send(KafkaConstants.TOPIC_CREATED_WORKSHEET, worksheetMapper.toWorksheetCreatedEvent(response));
            return Either.right(response);
        });
    }

    public WorksheetResponse getWorksheet(String worksheetId) {
        return worksheetRepository.findById(worksheetId)
                .map(worksheetMapper::toResponse)
                .orElseThrow(() -> new WorksheetNotFoundException("Worksheet not found with worksheetId: " + worksheetId));
    }
}
