package com.anonymization.etl.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkContext;
import org.apache.spark.broadcast.Broadcast;
import java.io.Serializable;

@Slf4j
public class BroadcastFacade implements Serializable {

    private Broadcast<KafkaSink> kafkaSinkBroadcast;
    private Broadcast<RedisSink> redisSinkBroadcast;

    private BroadcastFacade() {}

    public static BroadcastFacade create(SparkContext sc) {
        BroadcastFacade facade = new BroadcastFacade();
        facade.kafkaSinkBroadcast = sc.broadcast(KafkaSink.apply(), KafkaSink.getClassTag());
        facade.redisSinkBroadcast = sc.broadcast(RedisSink.apply(), RedisSink.getClassTag());
        return facade;
    }

    public Broadcast<KafkaSink> getKafkaSinkBroadcast() {
        return kafkaSinkBroadcast;
    }

    public Broadcast<RedisSink> getRedisSinkBroadcast() {
        return redisSinkBroadcast;
    }

    public RedisSink redis() {
        return redisSinkBroadcast.getValue();
    }

    public KafkaSink kafka() {
        return kafkaSinkBroadcast.getValue();
    }
}
