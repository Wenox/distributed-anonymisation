package com.wenox.anonymization.shared_events_library.api;

public final class KafkaConstants {

    private KafkaConstants() {

    }

    public static final String TOPIC_CREATED_BLUEPRINT = "created-blueprint";
    public static final String TOPIC_CREATED_WORKSHEET = "created-worksheet";

    public static final String TOPIC_ANONYMIZATION_EXECUTION = "anonymization-execution";
    public static final String TOPIC_ANONYMIZATION_EXECUTION_SUCCESS = "anonymization-execution-success";
    public static final String TOPIC_ANONYMIZATION_EXECUTION_FAILURE = "anonymization-execution-failure";

    public static final String TOPIC_RESTORE_SUCCESS = "restore-success";
    public static final String TOPIC_RESTORE_FAILURE = "restore-failure";
    public static final String TOPIC_METADATA_SUCCESS = "metadata-success";
    public static final String TOPIC_METADATA_FAILURE = "metadata-failure";

    public static final String TOPIC_OPERATIONS = "operations";

    public static final String TOPIC_EXTRACTION_SUCCESS = "extraction-success";
    public static final String TOPIC_EXTRACTION_FAILURE = "extraction-failure";
    public static final String TOPIC_TRANSFORMATION_ANONYMIZE_SUCCESS = "transformation-anonymize-success";
    public static final String TOPIC_TRANSFORMATION_ANONYMIZE_FAILURE = "transformation-anonymize-failure";
    public static final String TOPIC_TRANSFORMATION_SCRIPT_SUCCESS = "transformation-script-success";
    public static final String TOPIC_TRANSFORMATION_SCRIPT_FAILURE = "transformation-script-failure";
    public static final String TOPIC_LOAD_SUCCESS = "load-success";
    public static final String TOPIC_LOAD_FAILURE = "load-failure";
}
