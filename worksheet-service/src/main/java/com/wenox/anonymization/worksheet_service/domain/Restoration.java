package com.wenox.anonymization.worksheet_service.domain;

public record Restoration(String restorationId,
                          String blueprintId,
                          String databaseName,
                          boolean isActive,
                          String runnerIp) {

}