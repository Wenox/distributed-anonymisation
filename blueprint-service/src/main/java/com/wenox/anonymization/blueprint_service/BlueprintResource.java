package com.wenox.anonymization.blueprint_service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintResource {

    private final BlueprintService blueprintService;

    @PostMapping
    public ResponseEntity<String> importBlueprint(@Valid ImportBlueprintRequest dto) {
        log.info("Creating blueprint. DTO : {}", dto);
        final String id = blueprintService.importBlueprint(dto);
        log.info("Returning id : {}", id);
        return ResponseEntity.accepted().body(id);
    }

}
