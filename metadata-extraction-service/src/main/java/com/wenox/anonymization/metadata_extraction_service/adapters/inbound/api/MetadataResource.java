package com.wenox.anonymization.metadata_extraction_service.adapters.inbound.api;

import com.wenox.anonymization.metadata_extraction_service.domain.exception.MetadataNotFoundException;
import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/metadata")
public class MetadataResource {

    private final MetadataFacade metadataFacade;

    @GetMapping
    public ResponseEntity<Metadata> getMetadataByBlueprintId(@RequestParam("blueprint_id") String blueprintId) {
        return ResponseEntity.ok(metadataFacade.getMetadataByBlueprintId(blueprintId));
    }

    @ExceptionHandler(MetadataNotFoundException.class)
    public ResponseEntity<String> handleMetadataNotFoundException(MetadataNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

