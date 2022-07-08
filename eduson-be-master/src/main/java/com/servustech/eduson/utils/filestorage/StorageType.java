package com.servustech.eduson.utils.filestorage;

public enum StorageType {
    FILE_SYSTEM, AWS_S3, MINIO;

    public boolean isFileSystem() {
        return this == FILE_SYSTEM;
    }

    public boolean isAWS() {
        return this == AWS_S3;
    }
    
    public boolean isMinio() {
        return this == MINIO;
    }
}
