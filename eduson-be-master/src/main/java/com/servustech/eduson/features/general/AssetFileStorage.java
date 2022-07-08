package com.servustech.eduson.features.general;

import com.servustech.eduson.utils.filestorage.FileStorageException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.io.IOException;

@Slf4j
@Configuration
public class AssetFileStorage {
  
	// @Value("${spring.file-service.path}")
	// private String path;

	public String store(MultipartFile file, String scope, String area) {
    String path = "/home/ubuntu/hls/files/";
		String filename = file.getOriginalFilename();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String uri = area + scope + filename;
		String timestampedUri = area + "_allfiles/" + scope + timestamp.getTime() + "_" + filename;

		File dest = new File(path + uri);
		File timestampedDest = new File(path + timestampedUri);

		log.debug("Destination for file: " + dest.getAbsolutePath());

		File parentFolder = dest.getParentFile();
		File timestampedParentFolder = timestampedDest.getParentFile();

		log.debug("Parent folder for file: " + parentFolder.getAbsolutePath());
		if (!parentFolder.exists()) {
			parentFolder.mkdirs();
		}
		if (!timestampedParentFolder.exists()) {
			timestampedParentFolder.mkdirs();
		}

		try {
			Files.copy(file.getInputStream(), Paths.get(dest.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(file.getInputStream(), Paths.get(timestampedDest.getAbsolutePath()));
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return uri;
	}

  public InputStream retrieve(String filename, String scope, String area) throws FileStorageException {
    String path = "/home/ubuntu/hls/files/";
		File file = new File(path + area + scope + filename);
		if (file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new FileStorageException("cant-create-fileinputstream " + area + scope + filename, e); // TODO messparam
			}
		} else {
			throw new FileStorageException("invalid-key-provided " + area + scope + filename); // TODO messparam
		}
	}
}
