package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.FailureResponse;
import com.wenox.anonymization.worksheet_service.WorksheetRepository;
import com.wenox.anonymization.worksheet_service.domain.*;
import com.wenox.anonymization.worksheet_service.exception.WorksheetNotFoundException;
import com.wenox.anonymization.worksheet_service.operation.shuffle.AddShuffleRequest;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultOperationService implements OperationService {

    private final OperationRepository operationRepository;
    private final WorksheetRepository worksheetRepository;
    private final OperationMapper operationMapper;

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

        // Customize the operation based on the operation type
        customizeOperation(request, operationType);

        // Save the operation and return the response
        return saveOperationAndReturnResponse(worksheet, request);
    }

    private <T extends AddOperationRequest> void customizeOperation(T request, OperationType operationType) {
        switch (operationType) {
            case SUPPRESSION:
                // Implement customization for suppression operation
                break;
            case SHUFFLE:
                // Implement customization for shuffle operation
                break;
            // Add more cases for other operation types
            default:
                throw new IllegalStateException("Unsupported operation type: " + operationType);
        }
    }

    private <T extends AddOperationRequest> Either<FailureResponse, AddOperationResponse> saveOperationAndReturnResponse(Worksheet worksheet, T request) {
        Operation operation = operationMapper.toOperation(worksheet, request);
        operationRepository.save(operation);
        return Either.right(operationMapper.toResponse(operation, worksheet));
    }
}
