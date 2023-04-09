package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Worksheet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorksheetRepository extends MongoRepository<Worksheet, String> {
}
