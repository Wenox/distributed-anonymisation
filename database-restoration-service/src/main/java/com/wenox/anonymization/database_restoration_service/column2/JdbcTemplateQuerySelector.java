package com.wenox.anonymization.database_restoration_service.column2;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class JdbcTemplateQuerySelector implements QuerySelector {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateQuerySelector(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ColumnTuple select(String tableName, String primaryKeyColumnName, String columnName) {
        return new ColumnTuple(jdbcTemplate.query(String.format("SELECT %s, %s FROM %s", primaryKeyColumnName, columnName, tableName),
                (rs, n) -> Pair.of(rs.getString(primaryKeyColumnName), rs.getString(columnName))
        ));
    }
}