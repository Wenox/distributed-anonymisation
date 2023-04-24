package com.wenox.anonymization.database_restoration_service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface DatabaseCreationHandler {

    void createDatabase(String dbName) throws IOException, InterruptedException, TimeoutException;
}