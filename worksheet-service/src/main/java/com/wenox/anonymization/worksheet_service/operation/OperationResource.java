package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.exception.InactiveRestorationException;
import com.wenox.anonymization.worksheet_service.exception.WorksheetNotFoundException;
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
        return operationService.addSuppression(worksheetId, dto).fold(
                failureResponse -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureResponse),
                ResponseEntity::ok
        );
    }

    @ExceptionHandler(WorksheetNotFoundException.class)
    public ResponseEntity<String> handleWorksheetNotFoundException(WorksheetNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
