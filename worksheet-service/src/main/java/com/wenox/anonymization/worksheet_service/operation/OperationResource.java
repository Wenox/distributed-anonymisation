package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.exception.WorksheetNotFoundException;
import com.wenox.anonymization.worksheet_service.operation.generalisation.AddGeneralisationRequest;
import com.wenox.anonymization.worksheet_service.operation.shuffle.AddShuffleRequest;
import com.wenox.anonymization.worksheet_service.operation.suppression.AddSuppressionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/worksheets")
public class OperationResource {

    private final OperationService operationService;

    @PutMapping("/{id}/suppression")
    public ResponseEntity<?> addSuppression(@PathVariable("id") String worksheetId, @Valid @RequestBody AddSuppressionRequest dto) {
        return operationService.addOperation(worksheetId, dto, OperationType.SUPPRESSION).fold(
                failureResponse -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponse),
                ResponseEntity::ok
        );
    }

    @PutMapping("/{id}/shuffle")
    public ResponseEntity<?> addShuffle(@PathVariable("id") String worksheetId, @Valid @RequestBody AddShuffleRequest dto) {
        return operationService.addOperation(worksheetId, dto, OperationType.SHUFFLE).fold(
                failureResponse -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponse),
                ResponseEntity::ok
        );
    }

    @PutMapping("/{id}/generalisation")
    public ResponseEntity<?> addGeneralisation(@PathVariable("id") String worksheetId, @Valid @RequestBody AddGeneralisationRequest dto) {
        return operationService.addOperation(worksheetId, dto, OperationType.GENERALISATION).fold(
                failureResponse -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponse),
                ResponseEntity::ok
        );
    }

    @ExceptionHandler(WorksheetNotFoundException.class)
    public ResponseEntity<String> handleWorksheetNotFoundException(WorksheetNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
