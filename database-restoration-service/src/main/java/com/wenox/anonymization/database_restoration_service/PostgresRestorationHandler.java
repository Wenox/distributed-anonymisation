package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.s3.S3Constants;
import com.wenox.anonymization.s3.api.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostgresRestorationHandler implements RestorationHandler {

    private final StorageService storageService;
    private final CommandFactory commandFactory;

    public void restoreScriptDump(String dbName) throws IOException, InterruptedException, TimeoutException {
        log.info("Restoring database {} from script", dbName);
        String s3fileName = dbName.startsWith("mirror-") ? dbName.substring("mirror-".length()) : dbName;
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, s3fileName)) {
            restoreDumpFromInputStream(inputStream, commandFactory.generateRestoreFromScriptCommand(dbName));
        }
    }

    public void restoreArchiveDump(String dbName) throws IOException, InterruptedException, TimeoutException {
        log.info("Restoring database {} from archive", dbName);
        String s3fileName = dbName.startsWith("mirror-") ? dbName.substring("mirror-".length()) : dbName;
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, s3fileName)) {
            restoreDumpFromInputStream(inputStream, commandFactory.generateRestoreFromArchiveCommand(dbName));
        }
    }

    private void restoreDumpFromInputStream(InputStream inputStream, List<String> command) throws IOException, InterruptedException, TimeoutException {
        int exitCode = new ProcessExecutor()
                .command(command)
                .redirectInput(inputStream)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(60, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException(String.format("Database restoration using command %s failed with exit code: %s", command, exitCode));
        }

        log.info("Successfully restored database using command {}", command);
    }
}
