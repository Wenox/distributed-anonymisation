package com.wenox.anonymization.blueprint_service.config;

import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintMessagePublisher;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import com.wenox.anonymization.blueprint_service.domain.ports.DumpRepository;
import com.wenox.anonymization.blueprint_service.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    BlueprintSagaService blueprintSagaService(BlueprintStatusUpdater blueprintStatusUpdater,
                                              DumpRepository dumpRepository) {
        return new DefaultBlueprintSagaService(
                blueprintStatusUpdater,
                dumpRepository
        );
    }

    @Bean
    BlueprintService blueprintService(BlueprintRepository blueprintRepository,
                                      DumpRepository dumpRepository,
                                      BlueprintStatusUpdater blueprintStatusUpdater) {
        return new DefaultBlueprintService(
                blueprintRepository,
                dumpRepository,
                blueprintStatusUpdater
        );
    }

    @Bean
    BlueprintStatusUpdater blueprintStatusUpdater(BlueprintRepository blueprintRepository,
                                                  BlueprintMessagePublisher blueprintMessagePublisher) {
        return new BlueprintStatusUpdater(
                blueprintRepository,
                blueprintMessagePublisher
        );
    }
}
