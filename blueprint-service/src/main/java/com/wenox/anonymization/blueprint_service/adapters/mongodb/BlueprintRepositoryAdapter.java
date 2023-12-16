package com.wenox.anonymization.blueprint_service.adapters.mongodb;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
class BlueprintRepositoryAdapter implements BlueprintRepository {

    private final BlueprintEntityRepository blueprintEntityRepository;

    @Override
    public Blueprint save(Blueprint blueprint) {
        BlueprintEntity entity = BlueprintEntity.fromDomain(blueprint);
        BlueprintEntity savedEntity = blueprintEntityRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Blueprint> findById(String id) {
        return blueprintEntityRepository.findById(id).map(BlueprintEntity::toDomain);
    }
}
