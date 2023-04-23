package com.anonymization.shared_streaming_library;

import com.anonymization.shared_streaming_library.poc.CustomStreamingQueryListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SparkConfig {

    @Bean
    public SparkConf sparkConf() {
        log.info("Creating spark config");
        return new SparkConf()
                .setAppName("SparkSuppressionService")
                .setMaster("local[*]");
    }

    @Bean
    public JavaSparkContext javaSparkContext(SparkConf sparkConf) {
        log.info("Creating spark context");
        return new JavaSparkContext(sparkConf);
    }

    @Bean
    public SparkSession sparkSession(JavaSparkContext javaSparkContext) {
        log.info("Creating spark session");
        SparkSession spark = SparkSession
                .builder()
                .sparkContext(javaSparkContext.sc())
                .appName("SparkSuppressionService")
                .getOrCreate();

        spark.streams().addListener(new CustomStreamingQueryListener());

        return spark;
    }
}
