package com.wenox.anonymization.database_restoration_service;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.wenox.anonymization.s3_file_manager.S3Constants;
import com.wenox.anonymization.s3_file_manager.api.StorageService;
import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostgresRestoreService implements RestoreService {

    @Value("${command.create-database.v1}")
    private String commandCreateDatabase;

    @Value("${command.restore-dump.from-archive.v1}")
    private String commandRestoreFromArchive;

    @Value("${command.restore-dump.from-script.v1}")
    private String commandRestoreFromScript;

    private final StorageService storageService;

    @Override
    public void restore(String databaseName, RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException {
        switch (restoreMode) {
            case ARCHIVE -> restoreFromArchive(databaseName);
            case SCRIPT -> restoreFromScript(databaseName);
            default -> throw new UnsupportedRestoreModeException("Unsupported database restore mode: " + restoreMode);
        }
    }

    private void restoreFromArchive(String databaseName) throws IOException, InterruptedException, TimeoutException {
        log.info("Restoring {} from archive.", databaseName);

        createDatabase(databaseName);

        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, databaseName)) {
            restoreArchiveDumpFromInputStream(inputStream, databaseName, "postgres", "postgres", "localhost", "5432");
        }
    }

    private void restoreFromScript(String databaseName) throws IOException, InterruptedException, TimeoutException {
        log.info("Restoring {} from script.", databaseName);

        createDatabase(databaseName);

        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, databaseName)) {
            restoreScriptDumpFromInputStream(inputStream, databaseName, "postgres", "postgres", "localhost", "5432");
        }
    }

    private void createDatabase(String databaseName) throws IOException, InterruptedException, TimeoutException {
        List<String> command = buildCreateDatabaseCommand(commandCreateDatabase, "localhost", "5432", "postgres", databaseName);

        int exitCode = new ProcessExecutor()
                .command(command)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(60, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        log.info("Database {} is now created.", databaseName);

        if (exitCode != 0) {
            throw new RuntimeException(String.format("Create %s database failed", databaseName));
        }
    }

    private void restoreArchiveDumpFromInputStream(InputStream inputStream, String dbName, String dbUsername, String dbPassword, String dbHost, String dbPort) throws IOException, InterruptedException, TimeoutException {
        List<String> command = buildRestoreCommand(commandRestoreFromArchive, dbHost, dbPort, dbUsername, dbName);

        int exitCode = new ProcessExecutor()
                .environment(Map.of("PGPASSWORD", dbPassword))
                .command(command)
                .redirectInput(inputStream)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(60, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException("Database restore from archive failed with exit code: " + exitCode);
        }
    }

    private void restoreScriptDumpFromInputStream(InputStream inputStream, String dbName, String dbUsername, String dbPassword, String dbHost, String dbPort) throws IOException, InterruptedException, TimeoutException {
        List<String> command = buildRestoreCommand(commandRestoreFromScript, dbHost, dbPort, dbUsername, dbName);

        int exitCode = new ProcessExecutor()
                .environment(Map.of("PGPASSWORD", dbPassword))
                .command(command)
                .redirectInput(inputStream)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(60, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException("Database restore from script failed with exit code: " + exitCode);
        }
    }

    private List<String> buildCreateDatabaseCommand(String commandTemplate, String dbHost, String dbPort, String dbUsername, String dbName) {
        String formattedCommand = MessageFormat.format(commandTemplate, dbHost, dbPort, dbUsername, dbName);
        return Arrays.asList(formattedCommand.split("\\s+"));
    }

    private List<String> buildRestoreCommand(String commandTemplate, String dbHost, String dbPort, String dbUsername, String dbName) {
        String formattedCommand = MessageFormat.format(commandTemplate, dbHost, dbPort, dbUsername, dbName);
        return Arrays.asList(formattedCommand.split("\\s+"));
    }
}

