package com.wenox.anonymization.database_restoration_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostgresDatabaseCreationService implements DatabaseCreationService {

    private final CommandFactory commandFactory;

    public void createDatabase(String dbName) throws IOException, InterruptedException, TimeoutException {
        log.info("Creating database {}", dbName);

        List<String> command = commandFactory.generateCreateDatabaseCommand(dbName);

        int exitCode = new ProcessExecutor()
                .command(command)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(60, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException(String.format("Create %s database using command %s failed with exit code: %d", dbName, command, exitCode));
        }

        log.info("Successfully created database {} using command %s", dbName);
    }
}