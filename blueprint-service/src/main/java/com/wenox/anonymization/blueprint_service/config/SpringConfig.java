package com.wenox.anonymization.blueprint_service.config;

import com.wenox.anonymization.blueprint_service.domain.ports.MessagePublisher;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import com.wenox.anonymization.blueprint_service.domain.ports.DumpRepository;
import com.wenox.anonymization.blueprint_service.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    BlueprintSagaService blueprintSagaService(BlueprintSagaStatusUpdater blueprintStatusUpdater,
                                              DumpRepository dumpRepository) {
        return new DefaultBlueprintSagaService(
                blueprintStatusUpdater,
                dumpRepository
        );
    }

    @Bean
    BlueprintService blueprintService(BlueprintRepository blueprintRepository,
                                      BlueprintSagaStatusUpdater blueprintStatusUpdater,
                                      MessagePublisher messagePublisher) {
        return new DefaultBlueprintService(
                blueprintRepository,
                blueprintStatusUpdater,
                messagePublisher
        );
    }

    @Bean
    BlueprintSagaStatusUpdater blueprintStatusUpdater(BlueprintRepository blueprintRepository) {
        return new BlueprintSagaStatusUpdater(blueprintRepository);
    }
}
