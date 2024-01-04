package com.wenox.anonymization.database_restoration_service.domain.ports;

import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;

import java.util.Optional;

public interface RestorationRepository {

    Optional<Restoration> findById(String id);

    Restoration save(Restoration restoration);
}
