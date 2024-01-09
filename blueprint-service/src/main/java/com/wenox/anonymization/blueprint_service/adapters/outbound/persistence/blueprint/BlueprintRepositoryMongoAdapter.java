package com.wenox.anonymization.blueprint_service.adapters.outbound.persistence.blueprint;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
@Slf4j
class BlueprintRepositoryMongoAdapter implements BlueprintRepository {

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

    @Override
    public Stream<Blueprint> fetchStaleBlueprints(LocalDateTime thresholdTime) {
        return blueprintEntityRepository.findByCreatedDateBefore(thresholdTime)
                .stream()
                .map(BlueprintEntity::toDomain);
    }

    @Override
    public List<Blueprint> saveAll(Stream<Blueprint> blueprints) {
        List<BlueprintEntity> entities = blueprints
                .map(BlueprintEntity::fromDomain)
                .toList();

        return blueprintEntityRepository.saveAll(entities)
                .stream()
                .map(BlueprintEntity::toDomain)
                .toList();
    }
}
