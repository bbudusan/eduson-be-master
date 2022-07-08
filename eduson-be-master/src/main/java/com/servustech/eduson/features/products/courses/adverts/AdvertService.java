package com.servustech.eduson.features.products.courses;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.categories.modules.ModuleService;
import com.servustech.eduson.features.categories.tags.TagService;
import com.servustech.eduson.features.categories.tags.dto.TagDto;
import com.servustech.eduson.features.categories.tags.Tag;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.courses.dto.AdvertDto;
//import com.servustech.eduson.features.products.courses.dto.AdvertPublicViewResponse;
import com.servustech.eduson.features.products.courses.dto.ProductLinksDto;
import com.servustech.eduson.features.products.courses.dto.CoursesForAdvertResponse;
import com.servustech.eduson.features.products.courses.CourseAdvert;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AdvertService {

	private final AdvertRepository advertRepository;
	private final AdvertConverter advertConverter;
	private final CourseConverter courseConverter;
	private final FileService fileService;

	public AdvertDto create(AdvertDto advertDto, User user, MultipartFile file
			) {
		var file2 = file == null ? fileService.findById(-1L) : fileService.saveWithFile(file);

		var advert = Advert.builder()
				.name(advertDto.getName())
				.description(advertDto.getDescription())
				.file(file2)
				.duration(advertDto.getDuration())
				.onclick(advertDto.getOnclick())
				.build();

		advertRepository.save(advert);
		return advertConverter.fromAdvertToAdvertDto(advert);
	}

	public void deleteAdvert(Long advertId) {
		var advert = findById(advertId);
		advertRepository.delete(advert);
	}

	public Advert findById(Long advertId) {
		return advertRepository.findById(advertId)
				.orElseThrow(() -> new NotFoundException("advert-w-id-not-exist"));
	}

	public AdvertDto findById2(Long advertId) {
		var advert = advertRepository.findById(advertId)
				.orElseThrow(() -> new NotFoundException("advert-w-id-not-exist")); // TODO
		return advertConverter.fromAdvertToAdvertDto(advert);
	}

	// public Page<AdvertPublicViewResponse> searchAdverts(List<Long> tagIdsp, List<Long> tagIdsn, String filterByName, Pageable pageable, User user) {
	// 	var sort = pageable.getSort();
	// 	var sortCount = 0;
	// 	for (Sort.Order order : sort) {
	// 		sortCount++;
	// 	}
	// 	if (sortCount == 0) {
	// 		Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
	// 		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
	// 	}
	// 	var coursePage = courseRepository.searchBy(tagIdsp, tagIdsn, filterByName, pageable); // TODO Pageable.unpaged() ???
	// 	return new PageImpl<>(courseConverter.fromCourseListToCoursePublicViewResponseList(coursePage.getContent(), user),
	// 			pageable, coursePage.getTotalElements());
	// }

	public Page<CourseAdvert> findAllByCourse(Long courseId, String filterByName, Pageable pageable) {
		var sort = pageable.getSort();
		var sortCount = 0;
		// for (Sort.Order order : sort) {
		// 	Sort sort2 = JpaSort.unsafe(order.getDirection(), "ca.advert." + order.getProperty()); // TODO aggregate
		// 	pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2); // TODO at end of foreach
		// 	sortCount++;
		// }
		// if (sortCount == 0) {
		// 	Sort sort2 = JpaSort.unsafe(Sort.Direction.ASC, "ca.priority").andUnsafe(Sort.Direction.DESC, "ca.advert.start");// wc.place
		// 																																																										// ASC,
		// 																																																										// wc.course.addedDate
		// 																																																										// DESC
		// 	pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		// }
		return advertRepository.findAdvertsByCourseAndFilterByName(courseId, filterByName, pageable);
	}

	public Page<Advert> findAllNotInCourse(Long courseId, String filterByName, Pageable pageable) {
		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			// Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "id");
			// pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		return advertRepository.findAdvertsNotInCourseAndFilterByName(courseId, filterByName, pageable);
	}

	public Page<AdvertDto> viewAdverts(String filterByName, Pageable pageable, User user) {

		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "id");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		var advertPage = advertRepository.findAllAndFilterByName(filterByName, pageable);

		var adverts = advertConverter.fromAdvertListToAdvertDtoList(advertPage.getContent());
		return new PageImpl<>(adverts, pageable, advertPage.getTotalElements());
	}

	public List<Advert> findAllByIds(List<Long> ids) {
		return advertRepository.findAllById(ids);
	}

	@Transactional
	public AdvertDto updateAdvert(Long advertId, AdvertDto request, MultipartFile file
			) {
		var advert = findById(advertId);

		advert.setName(request.getName());
		advert.setDescription(request.getDescription());
		advert.setDuration(request.getDuration());
		advert.setOnclick(request.getOnclick());

		if (file != null) {
			var file2 = fileService.saveWithFile(file);
			advert.setFile(file2);
		} else if (request.getFileId() != null) {
			advert.setFile(fileService.findById(request.getFileId()));
		} else {
			advert.setFile(fileService.findById(-1L));
		}

		return advertConverter.fromAdvertToAdvertDto(advert);

	}

	public List<CoursesForAdvertResponse> getCoursesForAdverts(Long advertId) {
		var advert = findById(advertId);
		var courses = advert.getCourses();
		List<CoursesForAdvertResponse> coursesForAdvertResponses = new ArrayList<>();
		courses.forEach(course -> {
			var response = courseConverter.toCoursesForAdvertResponse(course);
			coursesForAdvertResponses.add(response);
		});
		return coursesForAdvertResponses;
	}

	public List<Advert> findAll() {
		return advertRepository.findAll();
	}

	public ProductLinksDto hasLinks(Long id) {
    Pageable pageable = PageRequest.of(0, 1, JpaSort.unsorted());
		return ProductLinksDto.builder()
			.courses(advertRepository.findFirstCourse(id, pageable).size() > 0)
		.build();
	}

}
