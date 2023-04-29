package com.wenox.anonymization.shared_events_library.api;

public final class KafkaConstants {

    private KafkaConstants() {

    }

    public static final String TOPIC_CREATE_BLUEPRINT = "create-blueprint";
    public static final String TOPIC_OPERATIONS = "operations";
    public static final String TOPIC_OPERATION_SUCCESS = "operation-success";
    public static final String TOPIC_EXTRACTION_SUCCESS = "extraction-success";
    public static final String TOPIC_OPERATION_FAILURE = "operation-failure";
    public static final String TOPIC_RESTORE_SUCCESS = "restore-success";
    public static final String TOPIC_RESTORE_FAILURE = "restore-failure";
    public static final String TOPIC_METADATA_SUCCESS = "metadata-success";
    public static final String TOPIC_METADATA_FAILURE = "metadata-failure";
}
