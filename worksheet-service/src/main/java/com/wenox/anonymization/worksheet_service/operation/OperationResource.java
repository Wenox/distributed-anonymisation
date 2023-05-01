package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.exception.WorksheetNotFoundException;
import com.wenox.anonymization.worksheet_service.operation.generalisation.AddGeneralisationRequest;
import com.wenox.anonymization.worksheet_service.operation.shuffle.AddShuffleRequest;
import com.wenox.anonymization.worksheet_service.operation.suppression.AddSuppressionRequest;
import com.wenox.anonymization.worksheet_service.operation.suppression.SuppressionSettings;
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

    @PostMapping("/start-simulation")
    public ResponseEntity<String> send(@RequestParam("worksheet_id") String worksheetId, @RequestParam("number_of_tasks") Integer numberOfTasks) {
        AddSuppressionRequest dto = new AddSuppressionRequest();
        dto.setTable("employees");
        dto.setColumn("salary");
        SuppressionSettings settings = new SuppressionSettings();
        settings.setToken("*****");
        dto.setSettings(settings);

        for (int i = 0; i < numberOfTasks; i++) {
            operationService.asyncAddOperation(worksheetId, dto, OperationType.SUPPRESSION);
        }

        return ResponseEntity.ok("Started simulation");
    }

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
