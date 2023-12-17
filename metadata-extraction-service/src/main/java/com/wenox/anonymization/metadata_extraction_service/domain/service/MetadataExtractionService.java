package com.wenox.anonymization.metadata_extraction_service.domain.service;

import com.wenox.anonymization.metadata_extraction_service.domain.model.DatabaseConnection;
import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;

import java.sql.SQLException;

public interface MetadataExtractionService {

    Metadata extractMetadata(DatabaseConnection databaseConnection) throws SQLException;
}
