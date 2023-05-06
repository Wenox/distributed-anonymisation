package com.wenox.anonymization.database_restoration_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostgresExistsDatabaseService implements ExistsDatabaseService {

    private final CommandFactory commandFactory;

    @Value("${command.exists-database.timeout:30}")
    private Integer timeout;

    @Override
    public boolean existsDatabase(String dbName) throws IOException, InterruptedException, TimeoutException {
        log.info("Checking if database {} exists", dbName);

        String command = commandFactory.generateExistsDatabaseCommand(dbName);

        ProcessResult result = new ProcessExecutor()
                .command("bash", "-c", command)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(timeout, TimeUnit.SECONDS)
                .readOutput(true)
                .execute();

        if (result.getExitValue() != 0) {
            throw new ExistsDatabaseException(String.format("Cannot check if database exists using command %s as it failed with exit code: %s", command, result.getExitValue()));
        }

        if (result.getOutput().getUTF8().contains("1")) {
            log.info("Database {} exists", dbName);
            return true;
        }

        log.info("Database {} does not exist", dbName);
        return false;
    }
}
