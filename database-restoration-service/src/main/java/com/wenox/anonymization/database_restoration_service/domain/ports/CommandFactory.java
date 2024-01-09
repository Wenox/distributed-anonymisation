package com.wenox.anonymization.database_restoration_service.domain.ports;

import java.util.List;

public interface CommandFactory {

    List<String> generateCreateDatabaseCommand(String db);

    List<String> generateDropDatabaseCommand(String db);

    String generateExistsDatabaseCommand(String db);

    List<String> generateRestoreFromArchiveCommand(String db);

    List<String> generateRestoreFromScriptCommand(String db);
}
