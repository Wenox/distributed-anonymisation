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
public class CommandFactory {

    private final ConnectionProperties connectionProperties;
    private final CommandProperties commandProperties;
    private final static String WHITESPACE_PATTERN = "\\s+";

    public List<String> getCreateDatabaseCommand(String dbName) {
        return buildCreateDatabaseCommand(dbName);
    }

    public List<String> getRestoreFromArchiveCommand(String dbName) {
        return buildRestoreFromArchiveCommand(dbName);
    }

    public List<String> getRestoreFromScriptCommand(String dbName) {
        return buildRestoreFromScriptCommand(dbName);
    }

    private List<String> buildCreateDatabaseCommand(String dbName) {
        log.info("command properties: {}", commandProperties);
        log.info("connection properties: {}", connectionProperties);
        String formattedCommand = MessageFormat.format(
                commandProperties.getCreateDatabase(),
                connectionProperties.getHost(),
                connectionProperties.getPort(),
                connectionProperties.getUsername(),
                dbName
        );
        return Arrays.asList(formattedCommand.split(WHITESPACE_PATTERN));
    }

    private List<String> buildRestoreFromArchiveCommand(String dbName) {
        String formattedCommand = MessageFormat.format(
                commandProperties.getRestoreDump().getFromArchive(),
                connectionProperties.getHost(),
                connectionProperties.getPort(),
                connectionProperties.getUsername(),
                dbName);
        return Arrays.asList(formattedCommand.split(WHITESPACE_PATTERN));
    }

    private List<String> buildRestoreFromScriptCommand(String dbName) {
        String formattedCommand = MessageFormat.format(
                commandProperties.getRestoreDump().getFromScript(),
                connectionProperties.getHost(),
                connectionProperties.getPort(),
                connectionProperties.getUsername(),
                dbName);
        return Arrays.asList(formattedCommand.split(WHITESPACE_PATTERN));
    }
}
