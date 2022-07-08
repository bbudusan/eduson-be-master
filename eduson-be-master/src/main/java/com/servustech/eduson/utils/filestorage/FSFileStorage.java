package com.servustech.eduson.utils.filestorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

@Slf4j
public class FSFileStorage implements FileStorage {

	@Value("${spring.file-service.path}")
	private String path;

	@Value("${spring.file-service.domain}")
	private String domain;

	@Override
	public String store(MultipartFile file) {
		String uri = file.getOriginalFilename();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		uri = timestamp.getTime() + "_" + uri;

		File dest = new File(path + uri); // todo should we use relative path? will allow migrating fs folder

		log.debug("Destination for file: " + dest.getAbsolutePath());

		File parentFolder = dest.getParentFile();

		log.debug("Parent folder for file: " + parentFolder.getAbsolutePath());
		if (!parentFolder.exists()) {
			parentFolder.mkdirs();
		}

		try {
			Files.copy(file.getInputStream(), Paths.get(dest.getAbsolutePath()));
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return uri;
	}

	@Override
	public String store(java.io.File file) {

		String uri = null;
		try {
			uri = file.getName();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			uri = timestamp.getTime() + "_" + uri;

			File dest = new File(path + uri); // todo should we use relative path? will allow migrating fs folder

			log.debug("Destination for file: " + dest.getAbsolutePath());

			File parentFolder = dest.getParentFile();

			log.debug("Parent folder for file: " + parentFolder.getAbsolutePath());
			if (!parentFolder.exists()) {
				parentFolder.mkdirs();
			}

			Files.copy(new FileInputStream(file), Paths.get(dest.getAbsolutePath()));
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return uri;
	}

	@Override
	public InputStream retrieve(String key) throws FileStorageException {
		File file = new File(path + key);
		if (file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new FileStorageException("cant-create-fileinputstream " + key, e); // TODO messparam
			}
		} else {
			throw new FileStorageException("invalid-key-provided " + key); // TODO messparam
		}
	}

	@Override
	public boolean delete(String key) throws FileStorageException {
		File file = new File(path + key);
		if (file.exists()) {
			return file.delete();
		} else {
			// An exception is not thrown because we still need the entities to be deleted,
			// even if the file cannot be found.
			// throw new FileStorageException("File not found", "file.not.found");
			return false;
		}
	}

	public static String localFileDownload(String domain, String filename, String key) {
		return domain + "api/files/download?filename=" + filename + "&key=" + key;
	}

	@Override
	public String getFileUrl(String fileName) {
		return fileName;
	}

	@Override
	public void deleteFile(String path) {

	}
}
