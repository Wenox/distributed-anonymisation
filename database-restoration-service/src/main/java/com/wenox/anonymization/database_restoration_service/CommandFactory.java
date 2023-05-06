package com.wenox.anonymization.database_restoration_service;

import java.util.List;

public interface CommandFactory {

    List<String> generateCreateDatabaseCommand(String dbName);

    List<String> generateDropDatabaseCommand(String dbName);

    String generateExistsDatabaseCommand(String dbName);

    List<String> generateRestoreFromArchiveCommand(String dbName);

    List<String> generateRestoreFromScriptCommand(String dbName);
}
