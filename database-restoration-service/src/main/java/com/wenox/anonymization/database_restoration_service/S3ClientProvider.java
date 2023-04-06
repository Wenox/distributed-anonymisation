package com.wenox.anonymization.database_restoration_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Component
public class S3ClientProvider {

    @Autowired
    private AwsCredentialsProvider awsCredentialsProvider;

    public S3Client createS3Client() {
        return S3Client.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.EU_WEST_2)
                .build();
    }
}
