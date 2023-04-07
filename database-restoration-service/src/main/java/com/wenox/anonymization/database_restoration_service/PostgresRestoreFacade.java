package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class PostgresRestoreFacade implements RestoreFacade {

    private final RestoreService restoreService;

    public PostgresRestoreFacade(PostgresRestoreService restoreService) {
        this.restoreService = restoreService;
    }

    @Override
    public void restore(String databaseName, RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException {
        restoreService.restore(databaseName, restoreMode);
    }
}
