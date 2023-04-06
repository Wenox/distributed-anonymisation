package com.wenox.anonymization.database_restoration_service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface RestoreService {

    void restore(final String databaseName, final RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException;
}
