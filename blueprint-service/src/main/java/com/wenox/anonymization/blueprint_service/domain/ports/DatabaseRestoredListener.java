package com.wenox.anonymization.blueprint_service.domain.ports;

import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;

public interface DatabaseRestoredListener {

    void onRestoreSuccess(DatabaseRestoredSuccessEvent event);

    void onRestoreFailure(DatabaseRestoredFailureEvent event);
}
