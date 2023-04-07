package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.metadata_extraction_service.domain.Metadata;

import java.sql.SQLException;

public interface MetadataExtractor {

    Metadata extractMetadata(DatabaseConnection databaseConnection) throws SQLException;
}
