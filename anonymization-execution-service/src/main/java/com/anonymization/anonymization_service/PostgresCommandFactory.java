package com.anonymization.anonymization_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostgresCommandFactory implements CommandFactory {

    private final static String WHITESPACE_PATTERN = "\\s+";

    @Value("${command.execute-script}")
    private String command;

    @Value("${command.generate-dump.archive-format}")
    private String dumpToArchiveCommand;

    @Value("${command.generate-dump.script-format}")
    private String dumpToScriptCommand;

    @Value("${command.generate-dump.directory-format}")
    private String dumpToDirectory;

    private final ConnectionProperties connectionProperties;

    @Override
    public List<String> generateExecuteScriptCommand(String db) {
        return buildExecuteScriptCommand(db);
    }

    private List<String> buildExecuteScriptCommand(String db) {
        return buildCommand(command, db);
    }

    @Override
    public List<String> generateDumpToArchiveCommand(String db) {
        return buildCommand(dumpToArchiveCommand, db);
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

}

