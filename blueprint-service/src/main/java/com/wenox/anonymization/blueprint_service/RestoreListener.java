package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.shared_events_library.DatabaseRestoredFailureEvent;
import com.wenox.anonymization.shared_events_library.DatabaseRestoredSuccessEvent;

public interface RestoreListener {

    void onRestoreSuccess(DatabaseRestoredSuccessEvent event);

    void onRestoreFailure(DatabaseRestoredFailureEvent event);
}
