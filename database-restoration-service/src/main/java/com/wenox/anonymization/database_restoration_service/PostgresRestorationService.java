package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.s3_file_manager.S3Constants;
import com.wenox.anonymization.s3_file_manager.api.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class PostgresRestorationService implements RestorationService {

    private final StorageService storageService;
    private final CommandFactory commandFactory;

    public void restoreScriptDump(String dbName) throws IOException, InterruptedException, TimeoutException {
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, dbName)) {
            restoreDumpFromInputStream(inputStream, dbName, commandFactory.generateRestoreFromScriptCommand(dbName));
        }
    }

    public void restoreArchiveDump(String dbName) throws IOException, InterruptedException, TimeoutException {
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, dbName)) {
            restoreDumpFromInputStream(inputStream, dbName, commandFactory.generateRestoreFromArchiveCommand(dbName));
        }
    }

    private void restoreDumpFromInputStream(InputStream inputStream, String dbName, List<String> command) throws IOException, InterruptedException, TimeoutException {
        int exitCode = new ProcessExecutor()
                .command(command)
                .redirectInput(inputStream)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(60, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException("Database restore failed with exit code: " + exitCode);
        }
    }
}
