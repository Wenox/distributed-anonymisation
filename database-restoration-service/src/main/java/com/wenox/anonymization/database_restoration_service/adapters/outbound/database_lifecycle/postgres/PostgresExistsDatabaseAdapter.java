package com.wenox.anonymization.database_restoration_service.adapters.outbound.database_lifecycle.postgres;

import com.wenox.anonymization.database_restoration_service.domain.exception.ExistsDatabaseException;
import com.wenox.anonymization.database_restoration_service.domain.ports.ExistsDatabasePort;
import com.wenox.anonymization.database_restoration_service.domain.ports.CommandFactory;
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
class PostgresExistsDatabaseAdapter implements ExistsDatabasePort {

    private final CommandFactory commandFactory;

    @Value("${command.exists-database.timeout:30}")
    private Integer timeout;

    @Override
    public boolean existsDatabase(String db) throws IOException, InterruptedException, TimeoutException {
        log.info("Checking if database {} exists", db);

        String command = commandFactory.generateExistsDatabaseCommand(db);

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
            log.info("Database {} exists", db);
            return true;
        }

        log.info("Database {} does not exist", db);
        return false;
    }
}
