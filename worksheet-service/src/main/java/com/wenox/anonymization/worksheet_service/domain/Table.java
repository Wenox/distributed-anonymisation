package com.wenox.anonymization.worksheet_service.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Table {
    private String tableName;
    private Integer numberOfRows;
    private Integer numberOfColumns;
    private Map<String, Column> columns = new HashMap<>();
    private PrimaryKey primaryKey;
}
