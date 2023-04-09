package com.wenox.anonymization.worksheet_service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/worksheets")
public class WorksheetResource {

    private final DefaultWorksheetService defaultWorksheetService;

    @PostMapping
    public ResponseEntity<CreateWorksheetResponse> createWorksheet(@Valid @RequestBody CreateWorksheetRequest dto) {
        log.info("Creating worksheet. DTO : {}", dto);
        return ResponseEntity.ok(defaultWorksheetService.createWorksheet(dto));
    }
}
