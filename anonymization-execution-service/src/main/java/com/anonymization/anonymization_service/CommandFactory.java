package com.anonymization.anonymization_service;

import java.util.List;

public interface CommandFactory {

    List<String> generateExecuteScriptCommand(String dbName);
}