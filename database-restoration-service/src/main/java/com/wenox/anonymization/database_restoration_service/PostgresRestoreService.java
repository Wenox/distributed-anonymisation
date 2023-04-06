package com.wenox.anonymization.database_restoration_service;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.wenox.anonymization.s3_file_manager.S3Constants;
import com.wenox.anonymization.s3_file_manager.api.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Service
@Slf4j
public class PostgresRestoreService implements RestoreService {

    private final String postgresIpAddress;
    private final String postgresHostPort;
    private final StorageService storageService;

    public PostgresRestoreService(@Value("${POSTGRES_IP_ADDRESS:localhost}") String postgresIpAddress,
                                  @Value("${POSTGRES_HOST_PORT:5432}") String postgresHostPort,
                                  StorageService storageService) {
        this.postgresIpAddress = postgresIpAddress;
        this.postgresHostPort = postgresHostPort;
        this.storageService = storageService;
    }

    @Override
    public void restore(String databaseName, RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException {
        log.info("Restoring new {}...", databaseName);

        switch (restoreMode) {
            case ARCHIVE -> restoreFromArchive(databaseName);
        }

        log.info("Restored new {} successfully.", databaseName);
    }

    private void restoreFromArchive(String databaseName) throws IOException, InterruptedException, TimeoutException {
        log.info("Restoring {} from archive.", databaseName);

        int exitCode = ProcessExecutorFactory.newProcess(
                    "createdb",
                    "-h", postgresIpAddress,
                    "-p", postgresHostPort,
                    "-U", "postgres", "--no-password",
                    "-T", "template0",
                    databaseName
        ).execute().getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException("Create failed");
        }

        log.info("Database is now created.");

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(S3Constants.BUCKET_BLUEPRINTS)
                .key(databaseName)
                .build();

        log.info("getobjectrequest: {}", getObjectRequest);

        log.info("get object request created");

        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_BLUEPRINTS, databaseName)) {
            restoreDatabaseFromInputStream(inputStream, databaseName, "postgres", "postgres", "localhost", "5432");
        }
    }

    private void restoreDatabaseFromInputStream(InputStream inputStream, String dbName, String dbUsername, String dbPassword, String dbHost, String dbPort) throws IOException, InterruptedException, TimeoutException {
        List<String> command = Arrays.asList("pg_restore", "-h", dbHost, "-p", dbPort, "-U", dbUsername, "-d", dbName, "-v");

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

