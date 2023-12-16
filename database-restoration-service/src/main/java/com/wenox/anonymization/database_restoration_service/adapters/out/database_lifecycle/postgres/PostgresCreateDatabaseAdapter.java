package com.wenox.anonymization.database_restoration_service.adapters.out.database_lifecycle.postgres;

import com.wenox.anonymization.database_restoration_service.domain.exception.CreateDatabaseException;
import com.wenox.anonymization.database_restoration_service.domain.ports.CreateDatabasePort;
import com.wenox.anonymization.database_restoration_service.domain.ports.CommandFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
class PostgresCreateDatabaseAdapter implements CreateDatabasePort {

    private final CommandFactory commandFactory;

    @Value("${command.create-database.timeout:30}")
    private Integer timeout;

    @Override
    public void createDatabase(String dbName) throws IOException, InterruptedException, TimeoutException {
        log.info("Creating database {}", dbName);

        List<String> command = commandFactory.generateCreateDatabaseCommand(dbName);

        int exitCode = new ProcessExecutor()
                .command(command)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(timeout, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new CreateDatabaseException(String.format("Create database '%s' using command '%s' failed with exit code: %d", dbName, command, exitCode));
        }

        log.info("Successfully created database {} using command {}", dbName, command);
    }
}
