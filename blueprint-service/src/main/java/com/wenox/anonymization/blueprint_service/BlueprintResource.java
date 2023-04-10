package com.wenox.anonymization.blueprint_service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintResource {

    private final BlueprintService blueprintService;

    @PostMapping
    public ResponseEntity<String> importBlueprint(@Valid ImportBlueprintRequest dto) {
        return ResponseEntity.accepted().body(blueprintService.importBlueprint(dto));
    }

    @GetMapping
    public ResponseEntity<Blueprint> getBlueprint(@RequestParam("blueprint_id") String blueprintId) {
        throw new BlueprintNotFoundException("Blueprint not found with blueprintId: " + blueprintId);
//        return ResponseEntity.ok(blueprintService.getBlueprint(blueprintId));
    }

    @ExceptionHandler(BlueprintNotFoundException.class)
    public ResponseEntity<String> handleBlueprintNotFoundException(BlueprintNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
