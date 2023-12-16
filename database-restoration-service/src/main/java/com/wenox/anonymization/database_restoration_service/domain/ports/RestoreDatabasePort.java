package com.wenox.anonymization.database_restoration_service.domain.ports;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface RestoreDatabasePort {

    void restoreScriptDump(String dbName) throws IOException, InterruptedException, TimeoutException;

    void restoreArchiveDump(String dbName) throws IOException, InterruptedException, TimeoutException;
}
