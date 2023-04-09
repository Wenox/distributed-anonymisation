package com.wenox.anonymization.worksheet_service.domain;

import java.util.Map;

public record Metadata(String blueprintId,
                       int numberOfTables,
                       Map<String, Table> tables) {
}
