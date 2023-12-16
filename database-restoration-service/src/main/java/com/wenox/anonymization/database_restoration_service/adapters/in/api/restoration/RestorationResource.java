package com.wenox.anonymization.database_restoration_service.adapters.in.api.restoration;

import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;
import com.wenox.anonymization.database_restoration_service.domain.exception.RestorationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/restorations")
class RestorationResource {

    private final RestorationFacade restorationFacade;

    @GetMapping
    ResponseEntity<Restoration> getRestorationByBlueprintId(@RequestParam("blueprint_id") String blueprintId) {
        return ResponseEntity.ok(restorationFacade.getRestorationByBlueprintId(blueprintId));
    }

    @ExceptionHandler(RestorationNotFoundException.class)
    ResponseEntity<String> handleRestorationNotFoundException(RestorationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
