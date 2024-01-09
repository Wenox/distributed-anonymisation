package com.anonymization.etl.core;

import com.anonymization.etl.domain.ColumnTuple;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class RedisConnectionSupplier implements Supplier<StatefulRedisConnection<String, ColumnTuple>>, Serializable {

    private final BroadcastSettings config;

    @Override
    public StatefulRedisConnection<String, ColumnTuple> get() {
        var connection = RedisUtils.buildRedisCommands(config.getRedisUrl());

        Runtime.getRuntime().addShutdownHook(new Thread(connection::close));

        return connection;
    }
}