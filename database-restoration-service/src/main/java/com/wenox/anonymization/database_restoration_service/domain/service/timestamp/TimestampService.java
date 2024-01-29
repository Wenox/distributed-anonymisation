package com.wenox.anonymization.database_restoration_service.domain.service.timestamp;

import com.wenox.anonymization.database_restoration_service.adapters.outbound.persistence.timestamp.TimestampRepository;
import com.wenox.anonymization.database_restoration_service.domain.model.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimestampService {

    private final TimestampRepository timestampRepository;

    public LocalDateTime getTimestamp() {
        return timestampRepository.findAll().stream()
                .findFirst()
                .orElseGet(this::createAndSaveNewTimestamp)
                .getTimestamp();
    }

    private Timestamp createAndSaveNewTimestamp() {
        Timestamp newTimestamp = new Timestamp();
        newTimestamp.setTimestamp(LocalDateTime.now());
        return timestampRepository.save(newTimestamp);
    }
    public void overrideTimestamp(LocalDateTime newTimestamp) {
        Timestamp timestamp = timestampRepository.findAll()
                .stream()
                .findFirst()
                .orElse(new Timestamp());

        timestamp.setTimestamp(newTimestamp);

        timestampRepository.save(timestamp);
    }
}
