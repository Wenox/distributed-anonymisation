package com.wenox.anonymization.s3_file_manager.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:s3-library.properties")
@Slf4j
public class S3Configuration {

    @Value("${aws.access_key_id:}")
    private String accessKeyId;

    @Value("${aws.secret_access_key:}")
    private String secretAccessKey;

    @Value("${aws.region:}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        log.info("Creating amazonS3 bean with accessKeyId: {}, secretAccessKey: {}, region: {}", accessKeyId, secretAccessKey, region);
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}