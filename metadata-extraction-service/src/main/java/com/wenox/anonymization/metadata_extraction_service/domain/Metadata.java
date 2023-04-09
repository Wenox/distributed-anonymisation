package com.wenox.anonymization.metadata_extraction_service.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document
public class Metadata {

  @Id
  private String blueprintId;

  private int numberOfTables = 0;
  private Map<String, Table> tables = new HashMap<>();

  public void insertTable(Table table) {
    tables.put(table.getTableName(), table);
    numberOfTables++;
  }

  public Table getTable(String table) {
    return tables.get(table);
  }

  public int getNumberOfTables() {
    return numberOfTables;
  }

  public void setNumberOfTables(int numberOfTables) {
    this.numberOfTables = numberOfTables;
  }

  public Map<String, Table> getTables() {
    return tables;
  }

  public void setBlueprintId(String blueprintId) {
    this.blueprintId = blueprintId;
  }

  public void setTables(Map<String, Table> tables) {
    this.tables = tables;
  }
}
