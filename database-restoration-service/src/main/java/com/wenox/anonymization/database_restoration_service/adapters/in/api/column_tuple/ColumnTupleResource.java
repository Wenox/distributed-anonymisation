package com.wenox.anonymization.database_restoration_service.adapters.in.api.column_tuple;

import com.wenox.anonymization.database_restoration_service.domain.exception.InactiveRestorationException;
import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/column-tuples")
class ColumnTupleResource {

    private final ColumnTupleFacade columnTupleFacade;

    @GetMapping
    ResponseEntity<ColumnTuple> getColumnTuple(@RequestParam("blueprint_id") String blueprintId,
                                                      @RequestParam("table") String table,
                                                      @RequestParam("column") String column,
                                                      @RequestParam("pk") String pk) {
        return ResponseEntity.ok(columnTupleFacade.queryColumnTuple(blueprintId, table, column, pk));
    }

    @ExceptionHandler(InactiveRestorationException.class)
    ResponseEntity<String> handleInactiveRestorationException(InactiveRestorationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
