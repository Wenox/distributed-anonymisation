package com.wenox.anonymization.blueprint_service.adapters.api;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.exception.BlueprintNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blueprints")
@RequiredArgsConstructor
class BlueprintApi {

    private final BlueprintFacade blueprintFacade;

    @PostMapping
    ResponseEntity<String> importBlueprint(@Valid ImportBlueprintRequest dto) {
        return ResponseEntity.accepted().body(blueprintFacade.importBlueprint(dto));
    }

    @GetMapping
    ResponseEntity<Blueprint> getBlueprint(@RequestParam("blueprint_id") String blueprintId) {
        return ResponseEntity.ok(blueprintFacade.getBlueprint(blueprintId));
    }

    @ExceptionHandler(BlueprintNotFoundException.class)
    ResponseEntity<String> handleBlueprintNotFoundException(BlueprintNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
