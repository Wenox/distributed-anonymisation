package com.wenox.anonymization.database_restoration_service.adapters.outbound.persistence.restoration;

import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;
import com.wenox.anonymization.database_restoration_service.domain.ports.RestorationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class RestorationRepositoryAdapter implements RestorationRepository {

    private final RestorationEntityRepository restorationEntityRepository;

    @Override
    public Optional<Restoration> findById(String id) {
        return restorationEntityRepository.findById(id).map(RestorationEntity::toDomain);
    }

    @Override
    public Restoration save(Restoration restoration) {
        RestorationEntity entity = RestorationEntity.fromDomain(restoration);
        return restorationEntityRepository.save(entity).toDomain();
    }
}
