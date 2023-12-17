package com.wenox.anonymization.metadata_extraction_service.domain.model;

public class Column {

  private String columnName;
  private String type; // todo: enum
  private boolean nullable;
  private boolean primaryKey;
  private boolean foreignKey;

  public Column(String columnName, String type, boolean nullable, boolean primaryKey, boolean foreignKey) {
    this.columnName = columnName;
    this.type = type;
    this.nullable = nullable;
    this.primaryKey = primaryKey;
    this.foreignKey = foreignKey;
  }

  public String getColumnName() {
    return columnName;
  }

  public String getType() {
    return type;
  }

  public boolean isNullable() {
    return nullable;
  }

  public boolean isPrimaryKey() {
    return primaryKey;
  }

  public boolean isForeignKey() {
    return foreignKey;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public void setPrimaryKey(boolean primaryKey) {
    this.primaryKey = primaryKey;
  }

  public void setForeignKey(boolean foreignKey) {
    this.foreignKey = foreignKey;
  }
}
