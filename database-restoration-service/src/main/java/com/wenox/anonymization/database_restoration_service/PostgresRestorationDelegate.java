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

    private final RestorationService restorationService;
    private final DatabaseCreationService databaseCreationService;

    @Override
    public void restore(String databaseName, RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException {
        switch (restoreMode) {
            case ARCHIVE -> restoreFromArchive(databaseName);
            case SCRIPT -> restoreFromScript(databaseName);
            default -> throw new UnsupportedRestoreModeException("Unsupported database restore mode: " + restoreMode);
        }
    }

    private void restoreFromArchive(String dbName) throws IOException, InterruptedException, TimeoutException {
        databaseCreationService.createDatabase(dbName);
        restorationService.restoreArchiveDump(dbName);
    }

    private void restoreFromScript(String dbName) throws IOException, InterruptedException, TimeoutException {
        databaseCreationService.createDatabase(dbName);
        restorationService.restoreScriptDump(dbName);
    }
}

