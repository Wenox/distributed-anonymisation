package com.anonymization.anonymization_service;

import java.util.List;

public interface CommandFactory {

    List<String> generateExecuteScriptCommand(String db);

    List<String> generateDumpToArchiveCommand(String db);
}