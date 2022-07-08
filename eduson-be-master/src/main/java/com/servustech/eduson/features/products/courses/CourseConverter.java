package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UsersConverter;
import com.servustech.eduson.features.products.courses.dto.CourseResponse;
import com.servustech.eduson.features.products.courses.dto.CourseResponseExt;
import com.servustech.eduson.features.products.courses.dto.CourseViewResponse;
import com.servustech.eduson.features.products.courses.dto.CoursePublicViewResponse;
import com.servustech.eduson.features.products.courses.dto.CourseViewResponseExt;
import com.servustech.eduson.features.products.courses.dto.CoursesView;
import com.servustech.eduson.features.products.webinars.WebinarCourse;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.courses.dto.CoursesForAdvertResponse;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

@Component
@AllArgsConstructor
public class CourseConverter {

	private final UsersConverter usersConverter;
	private final AdvertConverter advertConverter;
	private final PermissionsService permissionsService;
	private final Course4CheckRepository course4CheckRepository;

	public CourseViewResponse fromCourseToCourseViewResponse(Course course, User user) {
		if (course == null) {
			return new CourseViewResponse();
		}
		// TODO hasAccess if we need locked marks in lists
		return CourseViewResponse.builder()
				.id(course.getId())
				.name(course.getName())
				.price(course.getPrice())
				.duration(course.getDuration())
				.addedDate(course.getAddedDate())
				.addedBy(course.getAdmin().getFullName())
				.lector(course.getLector().getFullName())
				.favorited(course.isFavorited(user))
				.imageFile(course.getImageFile())
				.publishedDate(course.getPublishedDate())
				.published(course.getPublished())
				.build();

	}

	public CoursePublicViewResponse fromCourseToCoursePublicViewResponse(Course course, User user) {
		if (course == null) {
			return new CoursePublicViewResponse();
		}
		return CoursePublicViewResponse.builder()
				.id(course.getId())
				.name(course.getName())
				.price(course.getPrice())
				.duration(course.getDuration())
				.addedDate(course.getAddedDate())
				.lector(course.getLector().getFullName())
				.favorited(course.isFavorited(user))
				.imageFile(course.getImageFile())
				.build();
	}

	public CourseViewResponseExt fromCourseToCourseViewResponseExt(Course course, User user) {
		if (course == null) {
			return new CourseViewResponseExt();
		}
		var userHasAccess = permissionsService.hasAccessTo(user,
				course4CheckRepository.findById(course.getId()).orElseThrow(() -> new NotFoundException("course-w-id-not-exist")));
		return CourseViewResponseExt.builder()
				.id(course.getId())
				.name(course.getName())
				.price(course.getPrice())
				.addedDate(course.getAddedDate())
				.addedBy(course.getAdmin().getFullName())
				.lector(usersConverter.fromUserToUserDetailsResponse(course.getLector()))
				.favorited(course.isFavorited(user))
				.courseFile(userHasAccess ? course.getCourseFile() : null)
				.tags(course.getTags())
				.description(course.getDescription())
				.modules(course.getModules())
				.imageFile(course.getImageFile())
				.duration(course.getDuration())
				.publishedDate(course.getPublishedDate())
				.published(course.getPublished())
				.adverts(advertConverter.toAdvertDtoList(course.getAdverts()))
				.hasAccess(userHasAccess)
				.build();

	}

	public List<CourseViewResponse> fromCourseListToCourseViewResponseList(List<Course> courses, User user) {
		return courses
				.stream()
				.map(course -> fromCourseToCourseViewResponse(course, user))
				.collect(Collectors.toList());
	}

	public List<CoursePublicViewResponse> fromCourseListToCoursePublicViewResponseList(List<Course> courses, User user) {
		return courses
				.stream()
				.map(course -> fromCourseToCoursePublicViewResponse(course, user))
				.collect(Collectors.toList());
	}

	public CoursesView fromCourseToCoursesByLectorsViewResponse(Course course, User user) {

		return CoursesView.builder()
				.id(course.getId())
				.name(course.getName())
				.imageFile(course.getImageFile())
				.tags(course.getTags())
				.favorited(course.isFavorited(user))
				.build();

	}

	public List<CoursesView> fromCourseToCoursesByLectorsViewResponseList(List<Course> courses, User user) {
		return courses
				.stream()
				.map(course -> fromCourseToCoursesByLectorsViewResponse(course, user))
				.collect(Collectors.toList());
	}

	public CourseResponse toCourseResponse(WebinarCourse webinarCourse) {
		var course = webinarCourse.getCourse();
		return toCourseResponse2(course, true, webinarCourse.getPlace());
	}

	public CourseResponse toCourseResponse2(Course course, boolean isInWebinar, Long orderIndex) {
		var lector = course.getLector();

		return CourseResponse
				.builder()
				.id(course.getId())
				.name(course.getName())
				.price(course.getPrice())
				.duration(course.getDuration())
				.addedDate(course.getAddedDate())
				.addedBy(course.getAdmin().getFullName())
				.lector(course.getLector().getFullName())
				.isInWebinar(isInWebinar)
				.orderIndex(orderIndex)
				// .imageFile(course.getImageFile())
				.build();

	}

	public CourseResponseExt toCourseResponseExt(WebinarCourse webinarCourse, User user) {
		var course = webinarCourse.getCourse();
		var lector = course.getLector();
		var userHasAccess = permissionsService.hasAccessTo(user,
				course4CheckRepository.findById(course.getId()).orElseThrow(() -> new NotFoundException("course-w-id-not-exist")));
		return CourseResponseExt
				.builder()
				.courseId(course.getId())
				.name(course.getName())
				.addedDate(course.getAddedDate())
				.price(course.getPrice())
				.lector(usersConverter.fromUserToUserDetailsResponse(lector))
				.favorited(course.isFavorited(user))
				.orderIndex(webinarCourse.getPlace())
				.description(course.getDescription())
				.imageFile(course.getImageFile())
				.duration(course.getDuration())
				.tags(course.getTags())
				.adverts(advertConverter.toAdvertDtoList(course.getAdverts()))
				.hasAccess(userHasAccess)
				.build();

	}
	public CoursesForAdvertResponse toCoursesForAdvertResponse(Course course) {

		return CoursesForAdvertResponse
				.builder()
				.id(course.getId())
				.name(course.getName())
				.build();
	}

	public CoursesForAdvertResponse toCoursesForAdvertResponse(CourseAdvert courseAdvert) {

		return CoursesForAdvertResponse
				.builder()
				.id(courseAdvert.getCourse().getId())
				.name(courseAdvert.getCourse().getName())
				.build();
	}

}
