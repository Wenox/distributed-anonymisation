package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.metadata_extraction_service.domain.Column;
import com.wenox.anonymization.metadata_extraction_service.domain.Table;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ColumnExtractor {

    public static Column extractColumn(DatabaseMetaData extractor, Table table, ResultSet columns) throws SQLException {
        String columnName = columns.getString("COLUMN_NAME");
        String type = columns.getString("DATA_TYPE");
        String isNullable = columns.getString("IS_NULLABLE");
        boolean isPrimaryKey = table.getPrimaryKey().getColumnName().equals(columnName);
        boolean isForeignKey = checkForeignKey(extractor, table, columnName);
        return new Column(columnName, type, "YES".equals(isNullable), isPrimaryKey, isForeignKey);
    }

    private static boolean checkForeignKey(DatabaseMetaData extractor, Table table, String columnName) throws SQLException {
        Set<String> foreignKeyColumns = new HashSet<>();
        ResultSet importedKeys = extractor.getImportedKeys(null, "public", table.getTableName());
        while (importedKeys.next()) {
            foreignKeyColumns.add(importedKeys.getString("FKCOLUMN_NAME"));
        }
        return foreignKeyColumns.contains(columnName);
    }
}