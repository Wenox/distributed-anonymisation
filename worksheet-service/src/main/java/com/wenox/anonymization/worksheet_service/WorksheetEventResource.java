package com.wenox.anonymization.worksheet_service;


import com.wenox.anonymization.worksheet_service.domain.WorksheetCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/worksheet-events")
public class WorksheetEventResource {

    private final WorksheetCreatedEventRepository worksheetCreatedEventRepository;

    @GetMapping
    public ResponseEntity<List<WorksheetCreatedEvent>> getWorksheet(@RequestParam("timestamp") LocalDateTime timestamp) {
        return ResponseEntity.ok(worksheetCreatedEventRepository.findByCreatedAtAfter(timestamp));
    }
}
