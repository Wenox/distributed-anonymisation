package com.wenox.anonymization.blueprint_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
//@PropertySource("classpath:s3-library.properties")
//@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class S3Configuration {

    //    @Value("${aws.access_key_id:AKIAQJ6AVYSGW2V5UHGN}")
    private String accessKeyId = "AKIAQJ6AVYSGW2V5UHGN";

    //    @Value("${aws.secret_access_key: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}")
    private String secretAccessKey = "3soC+2pTrjyLB/vC+jrU/i6x522Po1/U7O+38wa5";

    //    @Value("${aws.region:}")
    private String region = "eu-west-2";

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .region(Region.of(region))
                .build();
    }
}