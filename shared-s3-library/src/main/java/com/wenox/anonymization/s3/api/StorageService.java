package com.wenox.anonymization.s3.api;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public interface StorageService extends Serializable {

    void uploadFile(MultipartFile file, String bucketName, String key) throws IOException;

    void uploadFile(byte[] data, String bucketName, String key);

    InputStream downloadFile(String bucketName, String objectName);
}
