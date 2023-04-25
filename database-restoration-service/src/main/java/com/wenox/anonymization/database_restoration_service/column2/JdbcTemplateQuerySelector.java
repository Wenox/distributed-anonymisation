package com.wenox.anonymization.database_restoration_service.column2;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplateQuerySelector implements QuerySelector {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateQuerySelector(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ColumnTuple select(String tableName, String primaryKeyColumnName, String columnName) {
        List<String> pks = new ArrayList<>();
        List<String> values = new ArrayList<>();

        jdbcTemplate.query(String.format("SELECT %s, %s FROM %s", primaryKeyColumnName, columnName, tableName),
                (RowMapper<Void>) (rs, rowNum) -> {
                    pks.add(rs.getString(primaryKeyColumnName));
                    values.add(rs.getString(columnName));
                    return null;
                });

        return new ColumnTuple(pks, values);
    }
}