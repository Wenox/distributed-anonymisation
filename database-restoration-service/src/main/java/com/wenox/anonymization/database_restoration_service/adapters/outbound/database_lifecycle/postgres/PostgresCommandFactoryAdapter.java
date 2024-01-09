package com.wenox.anonymization.database_restoration_service.adapters.outbound.database_lifecycle.postgres;

import com.wenox.anonymization.database_restoration_service.config.CommandProperties;
import com.wenox.anonymization.database_restoration_service.config.ConnectionProperties;
import com.wenox.anonymization.database_restoration_service.domain.ports.CommandFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
class PostgresCommandFactoryAdapter implements CommandFactory {

    private final static String WHITESPACE_PATTERN = "\\s+";

    private final ConnectionProperties connectionProperties;
    private final CommandProperties commandProperties;

    @Override
    public List<String> generateCreateDatabaseCommand(String db) {
        return buildCreateDatabaseCommand(db);
    }

    @Override
    public List<String> generateDropDatabaseCommand(String db) {
        return buildDropDatabaseCommand(db);
    }

    @Override
    public String generateExistsDatabaseCommand(String db) {
        return MessageFormat.format(
                commandProperties.getExistsDatabase(),
                connectionProperties.getHost(),
                connectionProperties.getPort(),
                connectionProperties.getUsername(),
                db
        );
    }

    @Override
    public List<String> generateRestoreFromArchiveCommand(String db) {
        return buildRestoreFromArchiveCommand(db);
    }

    @Override
    public List<String> generateRestoreFromScriptCommand(String db) {
        return buildRestoreFromScriptCommand(db);
    }

    private List<String> buildCommand(String commandTemplate, String db) {
        String formattedCommand = MessageFormat.format(
                commandTemplate,
                connectionProperties.getHost(),
                connectionProperties.getPort(),
                connectionProperties.getUsername(),
                db
        );

        return Arrays.asList(formattedCommand.split(WHITESPACE_PATTERN));
    }

    private List<String> buildCreateDatabaseCommand(String db) {
        return buildCommand(commandProperties.getCreateDatabase(), db);
    }

    private List<String> buildDropDatabaseCommand(String db) {
        return buildCommand(commandProperties.getDropDatabase(), db);
    }

    private List<String> buildRestoreFromArchiveCommand(String db) {
        return buildCommand(commandProperties.getRestoreDump().getFromArchive(), db);
    }

    private List<String> buildRestoreFromScriptCommand(String db) {
        return buildCommand(commandProperties.getRestoreDump().getFromScript(), db);
    }
}
