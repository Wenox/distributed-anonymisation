package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.metadata_extraction_service.domain.PrimaryKey;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrimaryKeyExtractor {
    public static PrimaryKey extractPrimaryKey(DatabaseMetaData extractor, String tableName, ResultSet PKs) throws SQLException {
        String columnName = PKs.getString("COLUMN_NAME");
        String primaryKeyName = PKs.getString("PK_NAME");
        ResultSet pkColumnResultSet = extractor.getColumns(null, "public", tableName, columnName);
        String type = "";
        if (pkColumnResultSet.next()) {
            type = pkColumnResultSet.getString("DATA_TYPE");
        }
        return new PrimaryKey(columnName, type, primaryKeyName);
    }
}