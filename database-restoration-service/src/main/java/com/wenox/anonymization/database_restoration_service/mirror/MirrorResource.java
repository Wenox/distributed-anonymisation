package com.wenox.anonymization.database_restoration_service.mirror;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mirrors")
public class MirrorResource {

    private final DefaultMirrorService defaultMirrorService;

    @PostMapping
    public ResponseEntity<CreateMirrorResponse> createMirror(@Valid @RequestBody CreateMirrorRequest dto) {
        return ResponseEntity.ok(defaultMirrorService.createMirror(dto));
    }
}
