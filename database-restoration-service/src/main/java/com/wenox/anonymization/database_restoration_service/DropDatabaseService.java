package com.wenox.anonymization.database_restoration_service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface DropDatabaseService {

    void dropDatabase(String dbName) throws IOException, InterruptedException, TimeoutException;
}
