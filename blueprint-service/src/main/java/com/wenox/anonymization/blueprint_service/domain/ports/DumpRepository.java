package com.wenox.anonymization.blueprint_service.domain.ports;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;

public interface DumpRepository {

    boolean uploadDump(byte[] content, Blueprint blueprint);

    void deleteDump(String blueprintId);
}
