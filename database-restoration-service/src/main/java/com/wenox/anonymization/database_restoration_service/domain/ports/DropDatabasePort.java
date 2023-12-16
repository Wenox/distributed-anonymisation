package com.wenox.anonymization.database_restoration_service.domain.ports;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface DropDatabasePort {

    void dropDatabase(String dbName) throws IOException, InterruptedException, TimeoutException;
}
