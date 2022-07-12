package com.servustech.eduson.features.general;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.files.FileService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class GeneralFileService {

	private final GeneralFileRepository generalRepository;
	private final FileService fileService;

	private static GeneralFileDto convert(GeneralFile general) {
		if (general == null) {
			return new GeneralFileDto();
		}
		return GeneralFileDto.builder()
			.id(general.getId())
			.key(general.getKey())
			.version(general.getVersion())
			.file(general.getFile())
			.build();
	} 

	public GeneralFileDto getId(Long id) {
		GeneralFile general = generalRepository.findById(id).orElseThrow(() -> new NotFoundException("general-file-id-not-found"));
		return convert(general);
	}
	public GeneralFileDto getKey(String key) {
		GeneralFile general = generalRepository.findFirstByKeyOrderByVersionDesc(key).orElseThrow(() -> new NotFoundException("general-file-key-not-found"));
		return convert(general);
	}
	public GeneralFileDto getKey(String key, User user) {
		return getKey(key);
	}
	public GeneralFileDto getKey(String key, Long version) {
		GeneralFile general = generalRepository.findByKeyAndVersion(key, version).orElseThrow(() -> new NotFoundException("general-file-key-version-not-found"));
		return convert(general);
	}
	public void delete(String key, Long version) {
		generalRepository.deleteAllByKeyAndVersion(key, version);
	}
	public void delete(Long id) {
		generalRepository.deleteById(id);
	}
	public void delete(String key) {
		generalRepository.deleteAllByKey(key);
	}
	@Transactional
	public GeneralFileDto create(GeneralFileDto generalDto, MultipartFile file) {
		var file2 = (file != null) ? fileService.saveWithFile(file) : fileService.findById(generalDto.getFileId());
		GeneralFile previous = generalRepository.findFirstByKeyOrderByVersionDesc(generalDto.getKey()).orElse(null);
		Long nextVersion = previous == null ? 1 : previous.getVersion() + 1;
		GeneralFile general = GeneralFile.builder()
			.key(generalDto.getKey())
			.version(nextVersion)
			.file(file2)
			.build();
		general = generalRepository.save(general);
		return convert(general);
	}

	public Page<GeneralFileDto> page(String filterByName, Pageable pageable) {
		if (filterByName == null) {
			filterByName = "";
		}
		var generalPage = generalRepository.findAllIdAndKeyAndVersionByKeyContaining(filterByName, pageable);
		List<GeneralFileDto> general = generalPage.stream().map(g -> convert(g)).collect(Collectors.toList());
		return new PageImpl<>(general, pageable, generalPage.getTotalElements());
	}

}
