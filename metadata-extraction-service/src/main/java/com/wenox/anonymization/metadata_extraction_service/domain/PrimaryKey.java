package com.wenox.anonymization.metadata_extraction_service.domain;

public class PrimaryKey extends Column {
  private String primaryKeyName;

  public PrimaryKey(String columnName, String type, String primaryKeyName) {
    super(columnName, type, false, true, false);
    this.primaryKeyName = primaryKeyName;
  }

  public String getPrimaryKeyName() {
    return primaryKeyName;
  }

  public void setPrimaryKeyName(String primaryKeyName) {
    this.primaryKeyName = primaryKeyName;
  }
}
