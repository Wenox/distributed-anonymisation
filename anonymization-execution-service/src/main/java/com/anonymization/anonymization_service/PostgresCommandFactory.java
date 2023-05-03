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

    private final ConnectionProperties connectionProperties;

    @Override
    public List<String> generateExecuteScriptCommand(String dbName) {
        return buildExecuteScriptCommand(dbName);
    }

    private List<String> buildExecuteScriptCommand(String dbName) {
        return buildCommand(command, dbName);
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

}

