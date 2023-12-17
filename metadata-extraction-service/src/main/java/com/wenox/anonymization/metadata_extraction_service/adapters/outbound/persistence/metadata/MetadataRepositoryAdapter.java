package com.wenox.anonymization.metadata_extraction_service.adapters.outbound.persistence.metadata;

import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;
import com.wenox.anonymization.metadata_extraction_service.domain.port.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetadataRepositoryAdapter implements MetadataRepository {

    private final MetadataEntityRepository metadataEntityRepository;

    @Override
    public Optional<Metadata> findByBlueprintId(String blueprintId) {
        return metadataEntityRepository.findByBlueprintId(blueprintId).map(MetadataEntity::toDomain);
    }

    @Override
    public void save(Metadata metadata) {
        MetadataEntity entity = MetadataEntity.fromDomain(metadata);
        metadataEntityRepository.save(entity);
    }
}
