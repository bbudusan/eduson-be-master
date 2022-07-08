package com.servustech.eduson.features.products.webinars;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UsersConverter;
import com.servustech.eduson.features.products.webinars.dto.WebinarViewResponse;
import com.servustech.eduson.features.products.webinars.dto.WebinarPublicViewResponse;
import com.servustech.eduson.features.products.webinars.dto.WebinarViewResponseExt;
import com.servustech.eduson.features.products.webinars.dto.WebinarsForCourseResponse;
import com.servustech.eduson.features.permissions.PermissionsService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class WebinarsConverter {

	private final UsersConverter usersConverter;
	private final PermissionsService permissionsService;
	private final Webinar4CheckRepository webinar4CheckRepository;

	public WebinarViewResponse fromWebinarToWebinarViewResponse(Webinar webinar, User user) {
		if (webinar == null) {
			return new WebinarViewResponse();
		}
		return WebinarViewResponse.builder()
				.id(webinar.getId())
				.name(webinar.getName())
				.acronym(webinar.getAcronym())
				.credits(webinar.getCredits())
				.addedDate(webinar.getAddedDate())
				.startTime(webinar.getStartTime())
				.endTime(webinar.getEndTime())
				.addedBy(webinar.getAdmin().getFullName())
				.coordinators(webinar.getCoordinators().stream().map(User::getFullName).collect(Collectors.toList()))
				.favorited(webinar.isFavorited(user))
				.imageFile(webinar.getImageFile())
				.price(webinar.getPrice())
				.published(webinar.getPublished())
				.build();
	}

	public WebinarPublicViewResponse fromWebinarToWebinarPublicViewResponse(Webinar webinar, User user) {
		if (webinar == null) {
			return new WebinarPublicViewResponse();
		}
		return WebinarPublicViewResponse.builder()
				.id(webinar.getId())
				.name(webinar.getName())
				.acronym(webinar.getAcronym())
				.credits(webinar.getCredits())
				.addedDate(webinar.getAddedDate())
				.startTime(webinar.getStartTime())
				.endTime(webinar.getEndTime())
				.coordinators(webinar.getCoordinators().stream().map(User::getFullName).collect(Collectors.toList()))
				.favorited(webinar.isFavorited(user))
				.imageFile(webinar.getImageFile())
				.price(webinar.getPrice())
				.build();
	}

	public WebinarViewResponseExt fromWebinarToWebinarViewResponseExt(Webinar webinar, User user) {
		if (webinar == null) {
			return new WebinarViewResponseExt();
		}
		var userHasAccess = permissionsService.hasAccessTo(user,
				webinar4CheckRepository.findById(webinar.getId()).orElseThrow(() -> new NotFoundException("webinar-w-id-not-exist")));

		return WebinarViewResponseExt.builder()
				.id(webinar.getId())
				.name(webinar.getName())
				.acronym(webinar.getAcronym())
				.credits(webinar.getCredits())
				.addedDate(webinar.getAddedDate())
				.startTime(webinar.getStartTime())
				.endTime(webinar.getEndTime())
				.addedBy(webinar.getAdmin().getFullName())
				.coordinators(usersConverter.fromUserToUserDetailsResponseList(webinar.getCoordinators()))
				.modules(webinar.getModules())
				.tags(webinar.getTags())
				.favorited(webinar.isFavorited(user))
				.description(webinar.getDescription())
				.price(webinar.getPrice())
				.imageFile(webinar.getImageFile())
				.published(webinar.getPublished())
				.hasAccess(userHasAccess)
				.build();
	}

	public List<WebinarViewResponse> fromWebinarListToWebinarViewResponseList(List<Webinar> webinars, User user) {
		return webinars
				.stream()
				.map(webinar -> fromWebinarToWebinarViewResponse(webinar, user))
				.collect(Collectors.toList());
	}

	public List<WebinarPublicViewResponse> fromWebinarListToWebinarPublicViewResponseList(List<Webinar> webinars,
			User user) {
		return webinars
				.stream()
				.map(webinar -> fromWebinarToWebinarPublicViewResponse(webinar, user))
				.collect(Collectors.toList());
	}

	public WebinarsForCourseResponse toWebinarsForCourseResponse(Webinar webinar) {

		return WebinarsForCourseResponse
				.builder()
				.webinarId(webinar.getId())
				.webinarName(webinar.getName())
				.build();
	}

	public WebinarsForCourseResponse toWebinarsForCourseResponse(WebinarCourse webinarCourse) {

		return WebinarsForCourseResponse
				.builder()
				.webinarId(webinarCourse.getWebinar().getId())
				.webinarName(webinarCourse.getWebinar().getName())
				.build();
	}
}
