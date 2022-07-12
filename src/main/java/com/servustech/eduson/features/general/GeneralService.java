package com.servustech.eduson.features.general;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import javax.transaction.Transactional;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class GeneralService {

	private final GeneralRepository generalRepository;

	private static GeneralDto convert(General general, boolean forPublic) {
		if (general == null) {
			return new GeneralDto();
		}
		return GeneralDto.builder()
			.id(forPublic ? 0 : general.getId())
			.confidential(forPublic ? null : general.isConfidential())
			.key(general.getKey())
			.version(forPublic ? 0 : general.getVersion())
			.langCode(forPublic ? null : general.getLangCode())
			.content(general.getContent())
			.build();
	} 

	public GeneralDto getId(Long id) {
		General general = generalRepository.findById(id).orElseThrow(() -> new NotFoundException("general-setting-id-not-found"));
		return convert(general, false);
	}
	public GeneralDto getKey(String key, String langCode, boolean forPublic) {
		General general = (forPublic ? generalRepository.findFirstByKeyAndLangCodeAndConfidentialOrderByVersionDesc(key, langCode, false) : generalRepository.findFirstByKeyAndLangCodeOrderByVersionDesc(key, langCode)).orElseThrow(() -> new NotFoundException("general-setting-key-lang-not-found"));
		// if (general.isConfidential() && forPublic) {
		// 	throw new NotFoundException("not-found");
		// }
		return convert(general, forPublic);
	}
	public GeneralDto getKey(String key, String langCode, User user) {
		boolean forPublic = user == null || !user.isAdmin();
		return getKey(key, langCode, forPublic);
	}
	public GeneralDto getKey(String key, Long version, String langCode) {
		General general = generalRepository.findByKeyAndVersionAndLangCode(key, version, langCode).orElseThrow(() -> new NotFoundException("general-setting-key-version-lang-not-found"));
		return convert(general, false);
	}
	public void delete(String key, Long version, String langCode) {
		generalRepository.deleteAllByKeyAndVersionAndLangCode(key, version, langCode);
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
	public GeneralDto create(GeneralDto generalDto) {
		General previous = generalRepository.findFirstByKeyAndLangCodeOrderByVersionDesc(generalDto.getKey(), generalDto.getLangCode()).orElse(null);
		Long nextVersion = previous == null ? 1 : previous.getVersion() + 1;
		General general = General.builder()
			.confidential(generalDto.getConfidential())
			.key(generalDto.getKey())
			.version(nextVersion)
			.langCode(generalDto.getLangCode() != null ? generalDto.getLangCode() : "ro")
			.content(generalDto.getContent())
			.build();
		general = generalRepository.save(general);
		return convert(general, false);
	}

	public Page<GeneralDto> page(String filterByName, String langCode, Pageable pageable, boolean isAdmin) {
		if (langCode == null) {
			langCode = "";
		}
		if (filterByName == null) {
			filterByName = "";
		}
		Page<General> generalPage = null;
		if (isAdmin) {
			generalPage = generalRepository.findAllIdAndConfidentialAndKeyAndVersionAndLangCodeByKeyContainingAndLangCodeContaining(filterByName, langCode, pageable);
		} else {
			generalPage = generalRepository.findAllIdAndConfidentialAndKeyAndVersionAndLangCodeByKeyContainingAndLangCodeContainingAndConfidential(filterByName, langCode, pageable, false);
		}
		List<GeneralDto> general = generalPage.stream().map(g -> convert(g, false)).collect(Collectors.toList());
		return new PageImpl<>(general, pageable, generalPage.getTotalElements());
	}
	// TODO nu toate setări trebuie să aibă limbă. deci, default "ro" nu e okay.

}
