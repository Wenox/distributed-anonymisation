package com.wenox.anonymization.database_restoration_service.domain.service.restoration_lifecycle;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface RestorationLifecycleService {

    void restore(final String db, final RestoreMode restoreMode) throws IOException, InterruptedException, TimeoutException;
}
