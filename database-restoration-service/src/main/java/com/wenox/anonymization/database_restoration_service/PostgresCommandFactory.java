package com.wenox.anonymization.database_restoration_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostgresCommandFactory implements CommandFactory {

    private final static String WHITESPACE_PATTERN = "\\s+";

    private final ConnectionProperties connectionProperties;
    private final CommandProperties commandProperties;

    public List<String> generateCreateDatabaseCommand(String dbName) {
        return buildCreateDatabaseCommand(dbName);
    }

    public List<String> generateRestoreFromArchiveCommand(String dbName) {
        return buildRestoreFromArchiveCommand(dbName);
    }

    public List<String> generateRestoreFromScriptCommand(String dbName) {
        return buildRestoreFromScriptCommand(dbName);
    }

    private List<String> buildCommand(String commandTemplate, String dbName) {
        String formattedCommand = MessageFormat.format(
                commandTemplate,
                connectionProperties.getHost(),
                connectionProperties.getPort(),
                connectionProperties.getUsername(),
                dbName
        );
        return Arrays.asList(formattedCommand.split(WHITESPACE_PATTERN));
    }

    private List<String> buildCreateDatabaseCommand(String dbName) {
        return buildCommand(commandProperties.getCreateDatabase(), dbName);
    }

    private List<String> buildRestoreFromArchiveCommand(String dbName) {
        return buildCommand(commandProperties.getRestoreDump().getFromArchive(), dbName);
    }

    private List<String> buildRestoreFromScriptCommand(String dbName) {
        return buildCommand(commandProperties.getRestoreDump().getFromScript(), dbName);
    }
}