package com.servustech.eduson.features.account.lectorTitle;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.account.lectorTitle.dto.LectorTitleDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LectorTitleService {
	
	private final LectorTitleRepository lectorTitleRepository;
	
	public LectorTitle saveTitle(LectorTitleDto titleDto) {
		var title = LectorTitle.builder()
							   .title(titleDto.getTitle())
							   .build();
		
		return lectorTitleRepository.save(title);
	}

	public LectorTitle findFirst() {
		return lectorTitleRepository.findFirstBy().orElse(null);
	}
	
	public void deleteTitle(Long titleId) {
		var lectorTitle = findById(titleId);
		lectorTitleRepository.delete(lectorTitle);
	}
	
	public LectorTitle findById(Long titleId) {
		return lectorTitleRepository.findById(titleId)
									.orElseThrow(
											() -> new NotFoundException("title-w-id-not-exist"));
	}
	
	public List<LectorTitle> getAllTitles(){
		return lectorTitleRepository.findAll();
	}
}
