package com.wenox.anonymization.database_restoration_service.adapters.restoration_lifecycle_adapter.postgres;

import com.wenox.anonymization.database_restoration_service.domain.exception.DropDatabaseException;
import com.wenox.anonymization.database_restoration_service.domain.ports.DropDatabasePort;
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

@Slf4j
@RequiredArgsConstructor
@Service
class PostgresDropDatabaseAdapter implements DropDatabasePort {

    private final CommandFactory commandFactory;

    @Value("${command.drop-database.timeout:30}")
    private Integer timeout;

    @Override
    public void dropDatabase(String dbName) throws IOException, InterruptedException, TimeoutException {
        log.info("Dropping database {}", dbName);

        List<String> command = commandFactory.generateDropDatabaseCommand(dbName);

        int exitCode = new ProcessExecutor()
                .command(command)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(timeout, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new DropDatabaseException(String.format("Drop database '%s' using command '%s' failed with exit code: %d", dbName, command, exitCode));
        }

        log.info("Successfully dropped database {} using command {}", dbName, command);
    }
}
