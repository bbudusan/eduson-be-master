package com.servustech.eduson.features.categories.modules;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.products.liveEvents.LiveEventsConverter;
import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import com.servustech.eduson.features.products.liveEvents.dto.LiveEventViewResponse;
import com.servustech.eduson.features.categories.modules.dto.ModuleDto;
import com.servustech.eduson.features.products.liveEvents.LiveEventRepository;
import com.servustech.eduson.features.products.webinars.WebinarsConverter;
import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.webinars.dto.WebinarViewResponse;
import com.servustech.eduson.features.products.webinars.WebinarRepository;
import com.servustech.eduson.features.products.courses.CourseConverter;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.products.courses.dto.CourseViewResponse;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import com.servustech.eduson.features.products.courses.CourseRepository;
import com.servustech.eduson.features.products.courses.dto.ProductLinksDto;
import com.servustech.eduson.exceptions.AlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ModuleService {
	
	private final ModuleRepository moduleRepository;
	private final ModuleWithContentRepository moduleWithContentRepository;
	private final LiveEventsConverter liveEventsConverter;
	private final WebinarsConverter webinarsConverter;
	private final CourseConverter courseConverter;
	private final CourseRepository courseRepository;
	private final WebinarRepository webinarRepository;
	private final LiveEventRepository liveEventRepository;

	public Module create(ModuleDto dto) {
		checkNameExisting(dto.getName());
		var module = Module
				.builder()
				.name(dto.getName())
				.published(dto.getPublished())
				.build();
		return moduleRepository.save(module);
	}
	
	public void deleteModule(Long id) {
		var module = findById(id);
		moduleRepository.delete(module);
	}
	
	public Module findById(Long moduleId) {
		return moduleRepository.findById(moduleId)
							   .orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
	}
	
	@Transactional
	public Module update(Long moduleId, ModuleDto dto) {
		var module = findById(moduleId);
		module.setName(dto.getName());
		module.setPublished(dto.getPublished());
		return module;
	}
	
	public Page<Module> getAllModules(String filterByName, Pageable pageable, User user) {
		if (filterByName == null) {
			filterByName = "";
		}
		return user != null && user.isAdmin() ? 
				moduleRepository.findAllByNameContaining(filterByName, pageable) :
				moduleRepository.findAllByPublishedAndNameContaining(filterByName, pageable);
	}
	
	private void checkNameExisting(String name) {
		moduleRepository.findByName(name).ifPresent(s -> {
			throw new AlreadyExistsException("module-w-id-already-exists");
		});
	}
	
	public List<Module> findAllByIds(List<Long> ids) {
		return ids == null ? new ArrayList<>() : moduleRepository.findAllById(ids);
	}

	public Page<LiveEventViewResponse> getLiveEvents(Long moduleId, String filterByName, Pageable pageable) {
		var liveEventPage = moduleRepository.getLiveEvents(moduleId, filterByName, pageable);
		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(liveEventPage.getContent(), null);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}
	public Page<LiveEventViewResponse> getLiveEventsUnassigned(Long moduleId, String filterByName, Pageable pageable) {
		var liveEventPage = moduleRepository.getLiveEventsUnassigned(moduleId, filterByName, pageable);
		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(liveEventPage.getContent(), null);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}
	public Page<WebinarViewResponse> getWebinars(Long moduleId, String filterByName, Pageable pageable) {
		var webinarPage = moduleRepository.getWebinars(moduleId, filterByName, pageable);
		var webinars = webinarsConverter.fromWebinarListToWebinarViewResponseList(webinarPage.getContent(), null);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}
	public Page<WebinarViewResponse> getWebinarsUnassigned(Long moduleId, String filterByName, Pageable pageable) {
		var webinarPage = moduleRepository.getWebinarsUnassigned(moduleId, filterByName, pageable);
		var webinars = webinarsConverter.fromWebinarListToWebinarViewResponseList(webinarPage.getContent(), null);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}
	// TODO filter out non-public courses if not admin
	public Page<CourseViewResponse> getCourses(Long moduleId, String filterByName, Pageable pageable) {
		var coursePage = moduleRepository.getCourses(moduleId, filterByName, pageable);
		var courses = courseConverter.fromCourseListToCourseViewResponseList(coursePage.getContent(), null);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}
	// TODO filter out non-public courses if not admin
	public Page<CourseViewResponse> getCoursesUnassigned(Long moduleId, String filterByName, Pageable pageable) {
		var coursePage = moduleRepository.getCoursesUnassigned(moduleId, filterByName, pageable);
		var courses = courseConverter.fromCourseListToCourseViewResponseList(coursePage.getContent(), null);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}
	@Transactional
	public void assignCourses(Long moduleId, AssignCoursesRequest coursesRequest) {
		var moduleWithContent = moduleWithContentRepository.findById(moduleId).orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
		var courses = courseRepository.findAllById(coursesRequest.getCoursesIds());
		moduleWithContent.addToCourses(courses);
	}
	@Transactional
	public void unassignCourses(Long moduleId, AssignCoursesRequest coursesRequest) {
		var moduleWithContent = moduleWithContentRepository.findById(moduleId).orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
		var courses = courseRepository.findAllById(coursesRequest.getCoursesIds());
		moduleWithContent.removeFromCourses(courses);
	}
	@Transactional
	public void assignWebinars(Long moduleId, AssignCoursesRequest webinarsRequest) {
		var moduleWithContent = moduleWithContentRepository.findById(moduleId).orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
		var webinars = webinarRepository.findAllById(webinarsRequest.getCoursesIds());
		moduleWithContent.addToWebinars(webinars);
	}
	@Transactional
	public void unassignWebinars(Long moduleId, AssignCoursesRequest webinarsRequest) {
		var moduleWithContent = moduleWithContentRepository.findById(moduleId).orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
		var webinars = webinarRepository.findAllById(webinarsRequest.getCoursesIds());
		moduleWithContent.removeFromWebinars(webinars);
	}
	@Transactional
	public void assignLiveEvents(Long moduleId, AssignCoursesRequest liveEventsRequest) {
		var moduleWithContent = moduleWithContentRepository.findById(moduleId)
				.orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
		var liveEvents = liveEventRepository.findAllById(liveEventsRequest.getCoursesIds());
		moduleWithContent.addToLiveEvents(liveEvents);
	}
	@Transactional
	public void unassignLiveEvents(Long moduleId, AssignCoursesRequest liveEventsRequest) {
		var moduleWithContent = moduleWithContentRepository.findById(moduleId)
				.orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
		var liveEvents = liveEventRepository.findAllById(liveEventsRequest.getCoursesIds());
		moduleWithContent.removeFromLiveEvents(liveEvents);
	}
	public ProductLinksDto hasLinks(Long id) {
    Pageable pageable = PageRequest.of(0, 1, JpaSort.unsorted());
		return ProductLinksDto.builder()
			.courses(moduleRepository.findFirstCourse(id, pageable).size() > 0)
			.webinars(moduleRepository.findFirstWebinar(id, pageable).size() > 0)
			.liveEvents(moduleRepository.findFirstLiveEvent(id, pageable).size() > 0)
			.subscribers(moduleRepository.findFirstPermission(id, pageable).size() > 0)
			.subscriptions(moduleRepository.findFirstSubscription(id, pageable).size() > 0)
		.build();
	}

	@Transactional
	public void togglePublish(Long moduleId) {
		var module  = findById(moduleId);
		var published  = module.getPublished();
		module.setPublished(!published);
	}
	@Transactional
	public void publish(Long moduleId) {
		var module  = findById(moduleId);
		module.setPublished(true);
	}
	@Transactional
	public void unpublish(Long moduleId) {
		var module  = findById(moduleId);
		module.setPublished(false);
	}
}
