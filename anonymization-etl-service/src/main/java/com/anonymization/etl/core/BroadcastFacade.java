package com.anonymization.etl.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkContext;
import org.apache.spark.broadcast.Broadcast;

import java.io.Serializable;

@Slf4j
public class BroadcastFacade implements Serializable {

    private Broadcast<KafkaSink> kafkaSinkBroadcast;
    private Broadcast<RedisSink> redisSinkBroadcast;
    private Broadcast<S3Sink> s3SinkBroadcast;

    private BroadcastFacade() {}

    public static BroadcastFacade create(BroadcastSettings config) {
        SparkContext sc = config.getSparkSession().sparkContext();
        BroadcastFacade facade = new BroadcastFacade();
        facade.kafkaSinkBroadcast = sc.broadcast(KafkaSink.apply(config), KafkaSink.getClassTag());
        facade.redisSinkBroadcast = sc.broadcast(RedisSink.apply(config), RedisSink.getClassTag());
        facade.s3SinkBroadcast = sc.broadcast(S3Sink.apply(config), S3Sink.getClassTag());
        return facade;
    }

    public Broadcast<KafkaSink> getKafkaSinkBroadcast() {
        return kafkaSinkBroadcast;
    }

    public Broadcast<RedisSink> getRedisSinkBroadcast() {
        return redisSinkBroadcast;
    }

    public Broadcast<S3Sink> getS3SinkBroadcast() {
        return s3SinkBroadcast;
    }

    public RedisSink redis() {
        return redisSinkBroadcast.getValue();
    }

    public KafkaSink kafka() {
        return kafkaSinkBroadcast.getValue();
    }

    public S3Sink s3() {
        return s3SinkBroadcast.getValue();
    }
}
