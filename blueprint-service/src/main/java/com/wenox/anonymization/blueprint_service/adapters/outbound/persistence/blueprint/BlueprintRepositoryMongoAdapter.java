package com.wenox.anonymization.blueprint_service.adapters.outbound.persistence.blueprint;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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

    @Value("${importing.dashboard.display-count:10}")
    private int dashboardDisplayCount;

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
    public List<Blueprint> fetchStaleBlueprints(LocalDateTime thresholdTime) {
        return blueprintEntityRepository.findByCreatedDateBefore(thresholdTime)
                .stream()
                .map(BlueprintEntity::toDomain)
                .toList();
    }

    @Override
    public List<Blueprint> saveAll(List<Blueprint> blueprints) {
        List<BlueprintEntity> entities = blueprints
                .stream()
                .map(BlueprintEntity::fromDomain)
                .toList();

        return blueprintEntityRepository.saveAll(entities)
                .stream()
                .map(BlueprintEntity::toDomain)
                .toList();
    }

    @Override
    public List<Blueprint> getBlueprintsForDashboard() {
        return blueprintEntityRepository.findTopByOrderByCreatedDateDesc(PageRequest.of(0, 5))
                .stream()
                .limit(dashboardDisplayCount)
                .map(BlueprintEntity::toDomain)
                .toList();
    }
}
