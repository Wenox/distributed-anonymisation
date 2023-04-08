package com.wenox.anonymization.database_restoration_service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface RestorationService {

    void restoreScriptDump(String dbName) throws IOException, InterruptedException, TimeoutException;

    void restoreArchiveDump(String dbName) throws IOException, InterruptedException, TimeoutException;
}
