package com.servustech.eduson.features.files;

import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.utils.filestorage.FileStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.io.InputStream;
import java.time.ZonedDateTime;

@Service
@AllArgsConstructor
@Transactional
public class FileService {

	private final FileRepository fileRepository;
	private final FileStorage fileStorage;

	public InputStream retrieve(String fileName) {
		return fileStorage.retrieve(fileName);
	}

	public String storeFile(MultipartFile file) {
		return fileStorage.store(file);
	}

	public String storeFile(java.io.File file) {
		return fileStorage.store(file);
	}

	public File saveWithFile(MultipartFile file) {
		var path = storeFile(file);

		var request = File.builder().originalFilename(file.getOriginalFilename()).path(path).uploadDate(ZonedDateTime.now())
				.build();
		return fileRepository.save(request);
	}
	public File saveWithFile(String originalFilename, String path) {
		var request = File.builder().originalFilename(originalFilename).path(path).uploadDate(ZonedDateTime.now())
				.build();
		return fileRepository.save(request);
	}

	public File findById(Long id) {
		return fileRepository.findById(id).orElseThrow(() -> new CustomException("file-with-id-does-not-exist")); // TODO why not notFoundException?
	}

	public String getUrl(Long id) {
		var file = findById(id);

		return fileStorage.getFileUrl(file.getPath());
	}

	public void update(File file, MultipartFile updateFile) {

		var path = storeFile(updateFile);
		if (file.getPath() != null) {
			fileStorage.deleteFile(file.getPath());
		}
		file.setPath(path);
		file.setOriginalFilename(updateFile.getOriginalFilename());
	}

}
