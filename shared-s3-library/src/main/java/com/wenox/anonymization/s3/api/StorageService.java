package com.wenox.anonymization.s3.api;

import java.io.InputStream;
import java.io.Serializable;

public interface StorageService extends Serializable {

    void uploadFile(byte[] data, String bucketName, String key);

    InputStream downloadFile(String bucketName, String key);

    void deleteFile(String bucketName, String key);
}
