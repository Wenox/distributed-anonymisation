package com.wenox.anonymization.database_restoration_service.domain.ports;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface RestoreDatabasePort {

    void restoreScriptDump(String db) throws IOException, InterruptedException, TimeoutException;

    void restoreArchiveDump(String db) throws IOException, InterruptedException, TimeoutException;
}
