package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.metadata_extraction_service.domain.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/metadata")
public class MetadataResource {

    private final MetadataService metadataService;

    @GetMapping
    public ResponseEntity<Metadata> getMetadataByBlueprintId(@RequestParam("blueprint_id") String blueprintId) {
//        throw new MetadataNotFoundException("Metadata not found with blueprintId: " + blueprintId);
        return ResponseEntity.ok(metadataService.getMetadataByBlueprintId(blueprintId));
    }

    @ExceptionHandler(MetadataNotFoundException.class)
    public ResponseEntity<String> handleMetadataNotFoundException(MetadataNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

