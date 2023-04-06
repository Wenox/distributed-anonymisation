package com.wenox.anonymization.database_restoration_service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface RestoreFacade {

    void restore(String databaseName, RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException;
}
