package com.wenox.anonymization.database_restoration_service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/restorations")
public class RestorationResource {

    private final RestorationService restorationService;

    @GetMapping
    public ResponseEntity<Restoration> getRestorationByBlueprintId(@RequestParam("blueprint_id") String blueprintId) {
        try { Thread.sleep(70000L); } catch (Exception ex) { }
        return ResponseEntity.ok(restorationService.getRestorationByBlueprintId(blueprintId));
    }

    @ExceptionHandler(RestorationNotFoundException.class)
    public ResponseEntity<String> handleRestorationNotFoundException(RestorationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
