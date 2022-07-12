package com.servustech.eduson.utils.filestorage;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

@AllArgsConstructor
@Slf4j
public class AWSFileStorage implements FileStorage {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.bucket}")
    private final String bucket;

    @Override
    public String store(MultipartFile file) {
        String key = file.getOriginalFilename();
        InputStream sourceFileIS = null;
        try {
            sourceFileIS = file.getInputStream();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, sourceFileIS, metadata);
        PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);
        log.debug("uploaded file to aws " + key + ", size: " + putObjectResult.getMetadata().getContentLength());
        IOUtils.closeQuietly(sourceFileIS);

        return key;
    }

    @Override
    public String store(java.io.File file) {
        String key = null;
        try {
            key = file.getName();
            InputStream sourceFileIS = null;
            sourceFileIS = new FileInputStream(file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(Files.size(Paths.get(file.getName())));
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, sourceFileIS, metadata);
            PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);
            log.debug("uploaded file to aws " + key + ", size: " + putObjectResult.getMetadata().getContentLength());
            IOUtils.closeQuietly(sourceFileIS);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return key;
    }

    @Override
    public InputStream retrieve(String key) throws FileStorageException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
        S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
        InputStream inputStream = s3Object.getObjectContent();

        return inputStream;
    }

    @Override
    public boolean delete(String key) throws FileStorageException {
        try {
            S3Object object = amazonS3Client.getObject(bucket, key);
            object.close();
        } catch (com.amazonaws.services.s3.model.AmazonS3Exception | IOException e) {
            // An exception is not thrown because we still need the entities to be deleted,
            // even if the file cannot be found.
            // throw new FileStorageException("File not found", "file.not.found");
            return false;
        }

        amazonS3Client.deleteObject(bucket, key);
        return true;
    }

    // used to temporary store csv reports in order to get temporary download url
    private String store(InputStream inputStream, String key) {
        try {
            // get input stream length for the putObject metadata
            byte[] bytes = IOUtils.toByteArray(inputStream);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            amazonS3Client.putObject(new PutObjectRequest(bucket, key, byteArrayInputStream, metadata));
            IOUtils.closeQuietly(byteArrayInputStream);

        } catch (IOException e) {
            throw new FileStorageException("cant-upload-csv", "file.upload.fail");
        }

        return key;
    }

    @Override
    public String getFileUrl(String fileName) {
        return fileName;
    }

    @Override
    public void deleteFile(String path) {

    }

}
