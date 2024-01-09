package com.wenox.anonymization.database_restoration_service.adapters.outbound.database_lifecycle.postgres;

import com.wenox.anonymization.database_restoration_service.domain.ports.CommandFactory;
import com.wenox.anonymization.database_restoration_service.domain.ports.RestoreDatabasePort;
import com.wenox.anonymization.s3.S3Constants;
import com.wenox.anonymization.s3.api.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// todo move timeouts to commandfactory, commandfactory methods should return COmmand instead of List<String>
// todo mirror-?
@Service
@Slf4j
@RequiredArgsConstructor
class PostgresRestoreDatabaseAdapter implements RestoreDatabasePort {

    private final StorageService storageService;
    private final CommandFactory commandFactory;

    @Value("${command.restore-dump.timeout:120}")
    private Integer timeout;

    @Override
    public void restoreScriptDump(String db) throws IOException, InterruptedException, TimeoutException {
        log.info("Restoring database {} from script", db);
        String s3fileName = db.startsWith("mirror-") ? db.substring("mirror-".length()) : db;
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, s3fileName)) {
            restoreDumpFromInputStream(inputStream, commandFactory.generateRestoreFromScriptCommand(db));
        }
    }

    @Override
    public void restoreArchiveDump(String db) throws IOException, InterruptedException, TimeoutException {
        log.info("Restoring database {} from archive", db);
        String s3fileName = db.startsWith("mirror-") ? db.substring("mirror-".length()) : db;
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, s3fileName)) {
            restoreDumpFromInputStream(inputStream, commandFactory.generateRestoreFromArchiveCommand(db));
        }
    }

    private void restoreDumpFromInputStream(InputStream inputStream, List<String> command) throws IOException, InterruptedException, TimeoutException {
        int exitCode = new ProcessExecutor()
                .command(command)
                .redirectInput(inputStream)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(timeout, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException(String.format("Database restoration using command %s failed with exit code: %s", command, exitCode));
        }

        log.info("Successfully restored database using command {}", command);
    }
}
