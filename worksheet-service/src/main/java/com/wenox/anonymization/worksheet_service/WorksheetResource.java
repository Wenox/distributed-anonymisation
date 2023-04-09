package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.CreateWorksheetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/worksheets")
public class WorksheetResource {

    private final DefaultWorksheetService defaultWorksheetService;

    @PostMapping
    public ResponseEntity<CreateWorksheetResponse> createWorksheet(@Valid @RequestBody CreateWorksheetRequest dto) {
        return ResponseEntity.ok(defaultWorksheetService.createWorksheet(dto));
    }
}
