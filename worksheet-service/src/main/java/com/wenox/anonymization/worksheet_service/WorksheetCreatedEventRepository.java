package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.WorksheetCreatedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorksheetCreatedEventRepository extends MongoRepository<WorksheetCreatedEvent, String> {

    List<WorksheetCreatedEvent> findByCreatedAtAfter(LocalDateTime timestamp);
}
