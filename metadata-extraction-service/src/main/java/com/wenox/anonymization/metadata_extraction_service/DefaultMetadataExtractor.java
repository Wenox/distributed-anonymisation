package com.wenox.anonymization.metadata_extraction_service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import com.wenox.anonymization.metadata_extraction_service.domain.Metadata;
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

        processTables(extractor, jdbcTemplate, metadata);

        log.warn("Metadata extracted successfully!");
        return metadata;
    }

    private void processTables(DatabaseMetaData extractor, JdbcTemplate jdbcTemplate, Metadata metadata) throws SQLException {
        ResultSet tables = extractor.getTables(null, "public", null, new String[]{"TABLE"});
        while (tables.next()) {
            Table table = createTable(tables, jdbcTemplate);
            metadata.insertTable(table);
            processPrimaryKeys(extractor, table);
            processColumns(extractor, table);
        }
    }

    private Table createTable(ResultSet tables, JdbcTemplate jdbcTemplate) throws SQLException {
        String tableName = tables.getString("TABLE_NAME");
        Integer numberOfRows = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
        Integer numberOfColumns = jdbcTemplate.queryForObject(getQueryForNumberOfColumnsInTable(tableName), Integer.class);
        return new Table(tableName, numberOfRows, numberOfColumns);
    }

    private void processPrimaryKeys(DatabaseMetaData extractor, Table table) throws SQLException {
        ResultSet PKs = extractor.getPrimaryKeys(null, "public", table.getTableName());
        while (PKs.next()) {
            table.setPrimaryKey(PrimaryKeyExtractor.extractPrimaryKey(extractor, table.getTableName(), PKs));
        }
    }

    private void processColumns(DatabaseMetaData extractor, Table table) throws SQLException {
        ResultSet columns = extractor.getColumns(null, "public", table.getTableName(), null);
        while (columns.next()) {
            table.insertColumn(ColumnExtractor.extractColumn(extractor, table, columns));
        }
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

