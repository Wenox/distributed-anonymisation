package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.database_restoration_service.column2.ColumnTuple;
import com.wenox.anonymization.database_restoration_service.column2.ColumnTupleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/restorations")
public class RestorationResource {

    private final RestorationService restorationService;
    private final ColumnTupleService columnTupleService;

    @GetMapping
    public ResponseEntity<Restoration> getRestorationByBlueprintId(@RequestParam("blueprint_id") String blueprintId) {
        return ResponseEntity.ok(restorationService.getRestorationByBlueprintId(blueprintId));
    }

    @GetMapping("/column-tuple")
    public ResponseEntity<ColumnTuple> getColumnTuple(@RequestParam("blueprint_id") String blueprintId,
                                                      @RequestParam("table") String table,
                                                      @RequestParam("column") String column,
                                                      @RequestParam("pk") String pk) {
        return ResponseEntity.ok(columnTupleService.queryColumnTuple(blueprintId, table, column, pk));
    }

    @PostMapping(value = "/mirror")
    public ResponseEntity<CreateMirrorResponse> createMirror(@Valid @RequestBody CreateMirrorRequest dto) {
        return ResponseEntity.ok(new CreateMirrorResponse("Stub response: " + dto.getWorksheetId()));
    }

    @ExceptionHandler(RestorationNotFoundException.class)
    public ResponseEntity<String> handleRestorationNotFoundException(RestorationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InactiveRestorationException.class)
    public ResponseEntity<String> handleInactiveRestorationException(InactiveRestorationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
