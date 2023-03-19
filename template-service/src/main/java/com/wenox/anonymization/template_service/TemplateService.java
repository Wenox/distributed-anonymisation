package com.wenox.anonymization.template_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public String createTemplate(CreateTemplateDto dto) {
        log.info("Create template service...");
        final var template = new Template();
        template.setTemplateDatabaseName("db-" + UUID.randomUUID());
        template.setTitle(dto.getTitle());
        template.setDescription(dto.getDescription());
        template.setCreatedDate(LocalDateTime.now());
        log.info("Saving : {}", template);
        templateRepository.save(template);

        log.info("Sending to kafka");
        kafkaTemplate.send("wenox-templates", template.getTemplateDatabaseName());
        log.info("Sent to kafka");

        return template.getId();
    }
}
