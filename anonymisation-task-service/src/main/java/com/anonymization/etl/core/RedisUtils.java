package com.anonymization.etl.core;

import com.anonymization.etl.ColumnTupleRedisCodec;
import com.anonymization.etl.domain.ColumnTuple;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

public class RedisUtils {

    public static StatefulRedisConnection<String, ColumnTuple> buildRedisCommands(String redisUrl) {
        RedisClient redisClient = RedisClient.create(redisUrl);
        return redisClient.connect(new ColumnTupleRedisCodec());
    }
}