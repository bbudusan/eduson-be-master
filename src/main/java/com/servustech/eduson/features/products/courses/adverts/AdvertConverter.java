package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UsersConverter;
import com.servustech.eduson.features.products.courses.dto.AdvertDto;
import com.servustech.eduson.features.products.courses.CourseAdvert;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.files.File;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.Time;


@Component
@AllArgsConstructor
public class AdvertConverter {

	private final AdvertRepository advertRepository;

	// public AdvertViewResponse fromAdvertToAdvertViewResponse(Advert advert) {
	// 	if (advert == null) {
	// 		return new AdvertViewResponse();
	// 	}
	// 	return AdvertViewResponse.builder()
	// 			.id(advert.getId())
	// 			.name(advert.getName())
	// 			.duration(advert.getDuration())
	// 			.file(advert.getFile())
	// 			.onclick(advert.getOnclick())
	// 			.build();
	// }
	public AdvertDto fromAdvertToAdvertDto(Advert advert) {
		if (advert == null) {
			return new AdvertDto();
		}
		return AdvertDto.builder()
				.id(advert.getId())
				.name(advert.getName())
				.duration(advert.getDuration())
				.file(advert.getFile())
				.onclick(advert.getOnclick())
	 			.description(advert.getDescription())
				.build();
	}

	// public AdvertPublicViewResponse fromAdvertToAdvertPublicViewResponse(Advert advert) {
	// 	if (advert == null) {
	// 		return new AdvertPublicViewResponse();
	// 	}
	// 	return AdvertPublicViewResponse.builder()
	// 			.id(advert.getId())
	// 			.name(advert.getName())
	// 			.duration(advert.getDuration())
	// 			.addedDate(advert.getAddedDate())
	// 			.lector(advert.getLector().getFullName())
	// 			.imageFile(advert.getImageFile())
	// 			.build();
	// }

	// public AdvertViewResponseExt fromAdvertToAdvertViewResponseExt(Advert course) {
	// 	if (course == null) {
	// 		return new AdvertViewResponseExt();
	// 	}
	// 	return AdvertViewResponseExt.builder()
	// 			.id(course.getId())
	// 			.name(course.getName())
	// 			.price(course.getPrice())
	// 			.addedDate(course.getAddedDate())
	// 			.addedBy(course.getAdmin().getFullName())
	// 			.courseFile(course.getFile())
	// 			.tags(course.getTags())
	// 			.description(course.getDescription())
	// 			.modules(course.getModules())
	// 			.imageFile(course.getImageFile())
	// 			.duration(course.getDuration())
	// 			.publishedDate(course.getPublishedDate())
	// 			.build();

	// }

	// public List<AdvertViewResponse> fromAdvertListToAdvertViewResponseList(List<Advert> adverts) {
	// 	return adverts
	// 			.stream()
	// 			.map(advert -> fromAdvertToAdvertViewResponse(advert))
	// 			.collect(Collectors.toList());
	// }
	public List<AdvertDto> fromAdvertListToAdvertDtoList(List<Advert> adverts) {
		return adverts
				.stream()
				.map(advert -> fromAdvertToAdvertDto(advert))
				.collect(Collectors.toList());
	}

	// public List<AdvertPublicViewResponse> fromAdvertListToAdvertPublicViewResponseList(List<Advert> adverts) {
	// 	return adverts
	// 			.stream()
	// 			.map(advert -> fromAdvertToAdvertPublicViewResponse(advert))
	// 			.collect(Collectors.toList());
	// }

	public List<AdvertDto> toAdvertDtoList(List<CourseAdvert> courseAdverts) {
		if (courseAdverts == null) {
			return null;
		}
		return courseAdverts
				.stream()
				.map(courseAdvert -> toAdvertDto(courseAdvert))
				.collect(Collectors.toList());
	}
	public AdvertDto toAdvertDto(CourseAdvert courseAdvert) {
		var advert = courseAdvert.getAdvert();
		return toAdvertDto2(advert, true, courseAdvert.getStart(), courseAdvert.getPriority(), courseAdvert.getRule());
	}
	public AdvertDto toAdvertDto2(Advert advert, boolean isInCourse, Time start, Integer priority, Long rule) {
		return AdvertDto
				.builder()
				.id(advert.getId())
				.name(advert.getName())
				.duration(advert.getDuration())
				.file(advert.getFile())
				.onclick(advert.getOnclick())
				.start(start)
				.priority(priority)
				.isInCourse(isInCourse)
				.rule(rule)
				.build();
	}

	// public AdvertResponseExt toAdvertResponseExt(CourseAdvert courseAdvert) {
	// 	var course = courseAdvert.getAdvert();
	// 	return AdvertResponseExt
	// 			.builder()
	// 			.courseId(course.getId())
	// 			.name(course.getName())
	// 			.addedDate(course.getAddedDate())
	// 			.price(course.getPrice())
	// 			.orderIndex(courseAdvert.getPlace())
	// 			.description(course.getDescription())
	// 			.courseFile(course.getAdvertFile())
	// 			.imageFile(course.getImageFile())
	// 			.duration(course.getDuration())
	// 			.tags(course.getTags())
	// 			.build();

	// }

}
