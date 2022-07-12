package com.servustech.eduson.features.account.lectors;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.account.AccountStatus;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.lectors.dto.LectorDto;
import com.servustech.eduson.features.account.lectorTitle.dto.LectorTitleDto;
import com.servustech.eduson.features.account.lectorTitle.LectorTitleService;
import com.servustech.eduson.features.account.lectorTitle.LectorTitle;
import com.servustech.eduson.features.account.lectors.dto.LectorProfileResponseDto;
import com.servustech.eduson.features.account.lectors.dto.LectorViewResponse;
import com.servustech.eduson.features.account.role.RoleService;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.security.auth.AuthService;
import com.servustech.eduson.features.account.PasswordValidator;
import com.servustech.eduson.exceptions.CustomException;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LectorService {

	private final LectorRepository lectorRepository;
	private final AuthService authService;
	private final FileService fileService;
	private PasswordEncoder passwordEncoder;
	private final LectorConverter lectorConverter;
	private final LectorTitleService lectorTitleService;
	private final RoleService roleService;

	public Lector findByUserId(Long userId) {
		var lector = lectorRepository.findByUserId(userId).orElse(null);
		return lector;
	}

	public Lector createDefault(User user) {
		LectorTitle lectorTitle = lectorTitleService.findFirst();
		if (lectorTitle == null) {
			lectorTitle = lectorTitleService.saveTitle(LectorTitleDto.builder().title("Dr.").build());
		}
		var lector = Lector.builder()
			.user(user)
			.description("")
			.hasAccess(false)
			.title(lectorTitle)
		.build();
		lectorRepository.save(lector);
		return lector;
	}


	@Transactional
	public LectorDto updateLector(LectorDto lectorDto, MultipartFile profilePicture) {
		var lector = findById(lectorDto.getId());
		var user = lector.getUser();
		if (!user.getUsername().equals(lectorDto.getUsername())) {
			authService.verifyIfUsernameOrEmailExists(lectorDto.getUsername(), null);
		}
		if (!user.getEmail().equals(lectorDto.getEmail())) {
			authService.verifyIfUsernameOrEmailExists(null, lectorDto.getEmail());
		}
		user.setUsername(lectorDto.getUsername());
		user.setFirstName(lectorDto.getFirstName());
		user.setLastName(lectorDto.getLastName());
		user.setEmail(lectorDto.getEmail());
		if (lectorDto.getPassword() != null) {
			if (!PasswordValidator.isValid(lectorDto.getPassword())) {
				throw new CustomException("password-too-easy");
			}
			user.setPassword(lectorDto.getPassword());
		}
		lector.setDescription(lectorDto.getDescription());
		lector.setHasAccess(lectorDto.isHasAccess());
		lector.setTitle(lectorTitleService.findById(lectorDto.getTitleId()));
		if (profilePicture != null) {
			var image = fileService.saveWithFile(profilePicture);
			user.setProfileImage(image);
		}
		if (lectorDto.isSendWelcomeEmail()) {
			authService.sendWelcomeEmail(user);
		}

		return lectorConverter.fromLectorToLectorDto(lector);
	}

	public Lector findById(Long lectorId) {
		return lectorRepository.findById(lectorId)
				.orElseThrow(() -> new NotFoundException("lector-w-id-not-exist"));
	}

	public LectorProfileResponseDto getLectorProfile(Long userId) {
		var findLector = lectorRepository.findByUserIdPub(userId).orElse(null);
		return lectorConverter.fromLectorToLectorProfile(findLector);
	}

	public LectorDto getLectorById(Long lectorId) {
		var lector = findById(lectorId);
		return lectorConverter.fromLectorToLectorDto(lector);
	}

	@Transactional
	public void giveAccess(Long lectorId) {
		var lector = findById(lectorId);
		lector.setHasAccess(true);
	}

	@Transactional
	public void removeAccess(Long lectorId) {
		var lector = findById(lectorId);
		lector.setHasAccess(false);
	}

	public void deleteLector(Long lectorId) {
		var lector = findById(lectorId);
		lectorRepository.delete(lector);
	}

	public Page<LectorViewResponse> viewLectors(String filterByName, Pageable pageable) {

		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.ASC, "user.firstName");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}

		if (filterByName == null) {
			filterByName = "";
		}
		var lectorPage = lectorRepository.findAllAndFilterByName(filterByName, true, pageable, roleService.getLectorRole().getId());

		var lectors = lectorConverter.fromLectorListToLectorViewResponseList(lectorPage.getContent());
		return new PageImpl<>(lectors, pageable,
				lectorPage.getTotalElements());
	}

	public Page<LectorProfileResponseDto> searchLectors(String filterByName, Pageable pageable) {

		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.ASC, "user.firstName");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}

		if (filterByName == null) {
			filterByName = "";
		}
		var lectorPage = lectorRepository.searchBy(filterByName, pageable, roleService.getLectorRole().getId());

		var lectors = lectorConverter.fromLectorToLectorProfileList(lectorPage.getContent());
		return new PageImpl<>(lectors, pageable,
				lectorPage.getTotalElements());
	}
	public Page<LectorProfileResponseDto> viewLectorsPublic(String filterByName, Pageable pageable) {

		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.ASC, "user.firstName");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}

		if (filterByName == null) {
			filterByName = "";
		}
		var lectorPage = lectorRepository.findAllAndFilterByName(filterByName, false, pageable, roleService.getLectorRole().getId());

		var lectors = lectorConverter.fromLectorToLectorProfileList(lectorPage.getContent());
		return new PageImpl<>(lectors, pageable,
				lectorPage.getTotalElements());
	}

	public List<LectorProfileResponseDto> getLectorsNarrow() {
		return lectorRepository
				.findAll().stream()
				.map(lector -> lectorConverter.fromLectorToLectorNarrow(lector))
				.collect(Collectors.toList());
	}
	public List<LectorProfileResponseDto> getLectorsWider() {
		return lectorRepository
				.findAllByPublishedIsNullOrPublishedIsTrue().stream()
				.map(lector -> lectorConverter.fromLectorToLectorWider(lector))
				.collect(Collectors.toList());
	}

	@Transactional
	public void togglePublish(Long lectorId) {
		var lector  = findById(lectorId);
		var published  = lector.getPublished();
		lector.setPublished(!published);
	}
	@Transactional
	public void publish(Long lectorId) {
		var lector  = findById(lectorId);
		lector.setPublished(true);
	}
	@Transactional
	public void unpublish(Long lectorId) {
		var lector  = findById(lectorId);
		lector.setPublished(false);
	}
}
