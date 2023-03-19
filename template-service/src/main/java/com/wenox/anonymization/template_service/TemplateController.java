package com.wenox.anonymization.template_service;

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
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<String> createTemplate(@Valid @RequestBody CreateTemplateDto createTemplateDto) {
        log.info("Creating template. DTO : {}", createTemplateDto);
        final String id = templateService.createTemplate(createTemplateDto);
        log.info("Returning id : {}", id);
        return ResponseEntity.accepted().body(id);
    }

}
