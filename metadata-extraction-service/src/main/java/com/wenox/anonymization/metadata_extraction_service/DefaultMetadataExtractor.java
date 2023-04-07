package com.wenox.anonymization.metadata_extraction_service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;

import com.wenox.anonymization.metadata_extraction_service.domain.Column;
import com.wenox.anonymization.metadata_extraction_service.domain.Metadata;
import com.wenox.anonymization.metadata_extraction_service.domain.PrimaryKey;
import com.wenox.anonymization.metadata_extraction_service.domain.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultMetadataExtractor implements MetadataExtractor {

    private final DataSourceFactory dataSourceFactory;

    @Override
    public Metadata extractMetadata(DatabaseConnection databaseConnection) throws SQLException {
        log.warn("Starting to extract metadata...");

        final DataSource dataSource = dataSourceFactory.getDataSource(databaseConnection);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final DatabaseMetaData extractor = dataSource.getConnection().getMetaData();
        final Metadata metadata = new Metadata();

        final ResultSet tables = extractor.getTables(null, "public", null, new String[] {"TABLE"});
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            Integer numberOfRows = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
            Integer numberOfColumns =
                    jdbcTemplate.queryForObject(getQueryForNumberOfColumnsInTable(tableName), Integer.class);
            Table table = new Table(tableName, numberOfRows, numberOfColumns);
            metadata.insertTable(table);

            final var PKs = extractor.getPrimaryKeys(null, "public", tableName);
            while (PKs.next()) {
                String columnName = PKs.getString("COLUMN_NAME");
                String primaryKeyName = PKs.getString("PK_NAME");
                final var pkColumnResultSet = extractor.getColumns(null, "public", tableName, columnName);
                String type = "";
                if (pkColumnResultSet.next()) {
                    type = pkColumnResultSet.getString("DATA_TYPE");
                }
                PrimaryKey primaryKey = new PrimaryKey(columnName, type, primaryKeyName);
                table.setPrimaryKey(primaryKey);
            }

            final Set<String> foreignKeyColumns = new HashSet<>();
            final var importedKeys = extractor.getImportedKeys(null, "public", tableName);
            while (importedKeys.next()) {
                foreignKeyColumns.add(importedKeys.getString("FKCOLUMN_NAME"));
            }

            final ResultSet columns = extractor.getColumns(null, "public", tableName, null);
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String type = columns.getString("DATA_TYPE");
                String isNullable = columns.getString("IS_NULLABLE");
                Column column = new Column(columnName, type, isNullable, table.getPrimaryKey().getColumnName().equals(columnName), foreignKeyColumns.contains(columnName));
                table.insertColumn(column);
            }
        }

        log.warn("Metadata extracted successfully!");
        return metadata;
    }

    private String getQueryForNumberOfColumnsInTable(String tableName) {
        return String.format(
                """
                    SELECT count(*)
                    FROM information_schema.columns
                    WHERE table_name = '%s'
                    """, tableName);
    }
}

