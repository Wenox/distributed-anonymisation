package com.wenox.anonymization.database_restoration_service.adapters.inbound.api.column_tuple;

import com.wenox.anonymization.database_restoration_service.domain.exception.InactiveRestorationException;
import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/column-tuples")
@Slf4j
class ColumnTupleResource {

    private final ColumnTupleFacade columnTupleFacade;

    @GetMapping
    ResponseEntity<ColumnTuple> getColumnTuple(@RequestParam("blueprint_id") String blueprintId,
                                                      @RequestParam("table") String table,
                                                      @RequestParam("column") String column,
                                                      @RequestParam("pk") String pk) {
        try {
            log.info("Waiting 3 seconds to simulate a delay in tasks processing - fragments generation...");
            Thread.sleep(3000L);
        } catch (Exception ex) {

        }
        return ResponseEntity.ok(columnTupleFacade.queryColumnTuple(blueprintId, table, column, pk));
    }

    @ExceptionHandler(InactiveRestorationException.class)
    ResponseEntity<String> handleInactiveRestorationException(InactiveRestorationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
