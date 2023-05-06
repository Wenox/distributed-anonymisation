package com.wenox.anonymization.database_restoration_service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface ExistsDatabaseService {

    boolean existsDatabase(String dbName) throws IOException, InterruptedException, TimeoutException;
}
