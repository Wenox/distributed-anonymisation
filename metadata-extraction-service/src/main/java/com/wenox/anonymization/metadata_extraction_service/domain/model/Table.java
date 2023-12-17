package com.wenox.anonymization.metadata_extraction_service.domain.model;

import java.util.HashMap;
import java.util.Map;

public class Table {
  private String tableName;
  private Integer numberOfRows;
  private Integer numberOfColumns;
  private Map<String, Column> columns = new HashMap<>();
  private PrimaryKey primaryKey;

  public Table(String tableName, Integer numberOfRows, Integer numberOfColumns) {
    this.tableName = tableName;
    this.numberOfRows = numberOfRows;
    this.numberOfColumns = numberOfColumns;
  }

  public void insertColumn(Column column) {
    columns.put(column.getColumnName(), column);
  }

  public String getTableName() {
    return tableName;
  }

  public Integer getNumberOfRows() {
    return numberOfRows;
  }

  public Integer getNumberOfColumns() {
    return numberOfColumns;
  }

  public Map<String, Column> getColumns() {
    return columns;
  }

  public PrimaryKey getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(PrimaryKey primaryKey) {
    this.primaryKey = primaryKey;
  }

  public void setColumns(Map<String, Column> columns) {
    this.columns = columns;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public void setNumberOfRows(Integer numberOfRows) {
    this.numberOfRows = numberOfRows;
  }

  public void setNumberOfColumns(Integer numberOfColumns) {
    this.numberOfColumns = numberOfColumns;
  }
}
