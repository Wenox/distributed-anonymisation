package com.wenox.anonymization.database_restoration_service.domain.service.restoration_lifecycle;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.wenox.anonymization.database_restoration_service.domain.exception.UnsupportedRestoreModeException;
import com.wenox.anonymization.database_restoration_service.domain.ports.CreateDatabasePort;
import com.wenox.anonymization.database_restoration_service.domain.ports.DropDatabasePort;
import com.wenox.anonymization.database_restoration_service.domain.ports.RestoreDatabasePort;
import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DefaultRestorationLifecycleService implements RestorationLifecycleService {

    private final DropDatabasePort dropDatabasePort;
    private final CreateDatabasePort createDatabasePort;
    private final RestoreDatabasePort restoreDatabasePort;

    @Override
    public void restore(String db, RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException {
        switch (restoreMode) {
            case ARCHIVE -> restoreFromArchive(db);
            case SCRIPT -> restoreFromScript(db);
            default -> throw new UnsupportedRestoreModeException(String.format("Error restoring database: %s because of unsupported database restore mode: %s", db, restoreMode));
        }
    }

    private void restoreFromArchive(String db) throws IOException, InterruptedException, TimeoutException {
        dropDatabasePort.dropDatabase(db);
        createDatabasePort.createDatabase(db);
        restoreDatabasePort.restoreArchiveDump(db);
    }

    private void restoreFromScript(String db) throws IOException, InterruptedException, TimeoutException {
        dropDatabasePort.dropDatabase(db);
        createDatabasePort.createDatabase(db);
        restoreDatabasePort.restoreScriptDump(db);
    }
}

