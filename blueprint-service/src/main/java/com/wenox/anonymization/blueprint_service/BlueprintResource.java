package com.wenox.anonymization.blueprint_service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintResource {

    private final BlueprintService blueprintService;

    @PostMapping
    public ResponseEntity<String> importBlueprint(@Valid ImportBlueprintRequest dto) {
        return ResponseEntity.accepted().body(blueprintService.importBlueprint(dto));
    }
}
