package com.wenox.anonymization.worksheet_service.domain;

import lombok.Data;

@Data
public class Column {

    private String columnName;
    private String type; // todo: enum
    private boolean nullable;
    private boolean primaryKey;
    private boolean foreignKey;
}
