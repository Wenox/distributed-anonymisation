package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.metadata_extraction_service.domain.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/metadata")
public class MetadataResource {

    private final MetadataService metadataService;

    @GetMapping("/{blueprintId}")
    public ResponseEntity<Metadata> getMetadata(@PathVariable("blueprintId") String blueprintId) {
        log.info("Retrieving metadata by blueprintId : {}", blueprintId);
        return ResponseEntity.ok(metadataService.getMetadataByBlueprintId(blueprintId));
    }
}

