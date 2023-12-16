package com.wenox.anonymization.database_restoration_service.domain.ports;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface ExistsDatabasePort {

    boolean existsDatabase(String dbName) throws IOException, InterruptedException, TimeoutException;
}
