package com.wenox.anonymization.database_restoration_service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostgresRestorationDelegate implements RestorationDelegate {

    private final RestorationHandler restorationHandler;
    private final DatabaseCreationHandler databaseCreationHandler;

    @Override
    public void restore(String dbName, RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException {
        switch (restoreMode) {
            case ARCHIVE -> restoreFromArchive(dbName);
            case SCRIPT -> restoreFromScript(dbName);
            default -> throw new UnsupportedRestoreModeException(String.format("Error restoring database: %s because of unsupported database restore mode: %s", dbName, restoreMode));
        }
    }

    private void restoreFromArchive(String dbName) throws IOException, InterruptedException, TimeoutException {
        databaseCreationHandler.createDatabase(dbName);
        restorationHandler.restoreArchiveDump(dbName);
    }

    private void restoreFromScript(String dbName) throws IOException, InterruptedException, TimeoutException {
        databaseCreationHandler.createDatabase(dbName);
        restorationHandler.restoreScriptDump(dbName);
    }
}

