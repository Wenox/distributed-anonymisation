package com.wenox.anonymization.database_restoration_service.adapters.out.column_tuple;

import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;
import com.wenox.anonymization.database_restoration_service.domain.ports.QuerySelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class JdbcTemplateQuerySelector implements QuerySelector {

    private final JdbcTemplate jdbcTemplate;

    JdbcTemplateQuerySelector(DataSource dataSource) {
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