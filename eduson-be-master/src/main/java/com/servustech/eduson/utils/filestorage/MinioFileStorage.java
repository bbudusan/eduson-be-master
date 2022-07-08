package com.servustech.eduson.utils.filestorage;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MinioFileStorage implements FileStorage {

	private final MinioClient minioClient;

	@Value("${cloud.minio.bucket}")
	private String bucket;

	@Override
	public String store(MultipartFile file) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String fileName = timestamp.getTime() + "_" + file.getOriginalFilename();
		try {
			minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(fileName)
					.stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
				| InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	@Override
	public String store(java.io.File file) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String fileName = null;
		try {
			fileName = timestamp.getTime() + "_" + file.getName();
			minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(fileName)
					.stream(new FileInputStream(file), Files.size(Paths.get(file.getName())), -1)
					.contentType(Files.probeContentType(Paths.get(file.getName()))).build());
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
				| InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	@Override
	public InputStream retrieve(String fileName) throws FileStorageException {

		var objectArgs = GetObjectArgs.builder().bucket(bucket).object(fileName).build();
		try {
			return minioClient.getObject(objectArgs);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
				| InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean delete(String key) throws FileStorageException {
		return false;
	}

	@Override
	public String getFileUrl(String fileName) {
		if (fileName == null) {
			return null;
		}
		String url = null;
		try {
			url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket)
					.object(fileName).expiry(2, TimeUnit.HOURS).build());
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
				| InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
			e.printStackTrace();
		}
		return url;
	}

	@Override
	public void deleteFile(String path) {
		try {
			minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(path).build());
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
				| InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
			e.printStackTrace();
		}
	}
}
