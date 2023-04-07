package com.wenox.anonymization.shared_events_library.api;

public final class KafkaConstants {

    private KafkaConstants() {

    }

    public static final String TOPIC_BLUEPRINT_CREATED = "wenox-blueprints";
    public static final String TOPIC_DATABASE_RESTORED_SUCCESS = "wenox-db-restored-success";
    public static final String TOPIC_DATABASE_RESTORED_FAILURE = "wenox-db-restored-failure";

}
