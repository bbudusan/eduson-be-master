package com.servustech.eduson.features.categories.tags;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.products.liveEvents.LiveEventsConverter;
import com.servustech.eduson.features.products.liveEvents.dto.LiveEventViewResponse;
import com.servustech.eduson.features.products.liveEvents.LiveEventRepository;
import com.servustech.eduson.features.products.webinars.dto.WebinarViewResponse;
import com.servustech.eduson.features.products.webinars.dto.WebinarPublicViewResponse;
import com.servustech.eduson.features.products.webinars.WebinarsConverter;
import com.servustech.eduson.features.products.webinars.WebinarRepository;
import com.servustech.eduson.features.products.courses.dto.CourseViewResponse;
import com.servustech.eduson.features.products.courses.dto.CoursePublicViewResponse;
import com.servustech.eduson.features.products.courses.CourseConverter;
import com.servustech.eduson.features.products.courses.CourseRepository;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import com.servustech.eduson.exceptions.AlreadyExistsException;
import com.servustech.eduson.features.categories.tagCategories.TagCategoryRepository;
import com.servustech.eduson.features.categories.tags.dto.TagDto;
import com.servustech.eduson.features.categories.tags.dto.TagViewResponse;
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
import java.util.Arrays;

@Service
@AllArgsConstructor
public class TagService {

	private final TagRepository tagRepository;
	private final TagCategoryRepository tagCategoryRepository;
	private final TagWithContentRepository tagWithContentRepository;
	private final LiveEventsConverter liveEventsConverter;
	private final WebinarsConverter webinarsConverter;
	private final CourseConverter courseConverter;
	private final CourseRepository courseRepository;
	private final WebinarRepository webinarRepository;
	private final LiveEventRepository liveEventRepository;

	public void deleteTag(Long id) {
		var tag = findById(id);
		tagRepository.delete(tag);
	}

	public Page<Tag> getAllTags(Pageable pageable) {
		return tagRepository.findAll(pageable);
	}

	public Tag create(TagDto tagDto) {

		checkNameExisting(tagDto.getName());

		var tagCategory = tagCategoryRepository.findById(tagDto.getTagCategoryId())
				.orElseThrow(() -> new NotFoundException(
						"tagcat-w-id-not-exist"));

		var tag = Tag
				.builder()
				.name(tagDto.getName())
				.tagCategory(tagCategory)
				.build();
		return tagRepository.save(tag);
	}

	@Transactional
	public Tag update(TagDto tagDto) {
		var tag = findById(tagDto.getId());
		tag.setName(tagDto.getName());
		return tag;
	}

	public List<Tag> getAll() {
		return tagRepository.findAll();
	}

	public Tag findById(Long tagId) {
		return tagRepository.findById(tagId)
				.orElseThrow(() -> new NotFoundException("tag-w-id-not-exist"));
	}

	private void checkNameExisting(String name) {
		tagRepository.findByName(name).ifPresent(s -> {
			throw new AlreadyExistsException("tag-w-id-already-exists");
		});
	}

	public Page<TagViewResponse> getTagsByTagCategory(Long tagCategoryId, String filterByName, Pageable pageable) {
		var tagCategory = tagCategoryRepository.findById(tagCategoryId)
				.orElseThrow(() -> new NotFoundException(
						"tagcat-w-id-not-exist"));

		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}

		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.ASC, "name");
			var pageNumber = pageable == Pageable.unpaged() ? 0 : pageable.getPageNumber();
			var pageSize = pageable == Pageable.unpaged() ? 100000 : pageable.getPageSize();
			pageable = PageRequest.of(pageNumber, pageSize, sort2); // TODO we should make request of unpaged and sort if possigble, no 100000s 
		}

		if (filterByName == null) {
			filterByName = "";
		}
		Page<Tag> tagsPage = tagRepository.findAllByTagCategoryAndNameContaining(tagCategory, filterByName, pageable);
		List<Tag> tags = tagsPage.getContent();
		List<TagViewResponse> tagDtos = new ArrayList<>();

		tags.forEach(tag -> {
			var response = TagViewResponse.builder()
					.name(tag.getName())
					.id(tag.getId())
					.courseCnt(tag.getCourseCnt())
					.webinarCnt(tag.getWebinarCnt())
					.eventCnt(tag.getEventCnt())
					.build();

			tagDtos.add(response);
		});

		return new PageImpl<>(tagDtos, pageable, tagsPage.getTotalElements());

	}

	public List<Tag> findAllByIds(List<Long> ids) {
		return ids == null ? new ArrayList<>() : tagRepository.findAllById(ids);
	}

	public Page<LiveEventViewResponse> getLiveEvents(Long tagId, String filterByName, Pageable pageable) {
		var liveEventPage = tagRepository.getLiveEvents(Arrays.asList(new Long[] { tagId }), null, null, filterByName, pageable);
		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(liveEventPage.getContent(), null);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}
	public Page<LiveEventViewResponse> getLiveEvents(List<Long> tagIdsp, List<Long> tagIdsn, List<Long> eventIdsn, String filterByName, Pageable pageable) {
		var liveEventPage = tagRepository.getLiveEvents(tagIdsp, tagIdsn, eventIdsn, filterByName, pageable);
		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(liveEventPage.getContent(), null);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}

	public Page<LiveEventViewResponse> getLiveEventsUnassigned(Long tagId, String filterByName, Pageable pageable) {
		var liveEventPage = tagRepository.getLiveEventsUnassigned(tagId, filterByName, pageable);
		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(liveEventPage.getContent(), null);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}

	public Page<WebinarViewResponse> getWebinars(Long tagId, String filterByName, Pageable pageable) {
		var webinarPage = tagRepository.getWebinars(Arrays.asList(new Long[] { tagId }), null, null, filterByName, pageable);
		var webinars = webinarsConverter.fromWebinarListToWebinarViewResponseList(webinarPage.getContent(), null);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}
	public Page<WebinarPublicViewResponse> getWebinars(List<Long> tagIdsp, List<Long> tagIdsn, List<Long> webinarIdsn, String filterByName, Pageable pageable) {
		var webinarPage = tagRepository.getWebinars(tagIdsp, tagIdsn, webinarIdsn, filterByName, pageable);
		var webinars = webinarsConverter.fromWebinarListToWebinarPublicViewResponseList(webinarPage.getContent(), null);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}

	public Page<WebinarViewResponse> getWebinarsUnassigned(Long tagId, String filterByName, Pageable pageable) {
		var webinarPage = tagRepository.getWebinarsUnassigned(tagId, filterByName, pageable);
		var webinars = webinarsConverter.fromWebinarListToWebinarViewResponseList(webinarPage.getContent(), null);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}

	// TODO filter out non-public courses if not admin
	public Page<CourseViewResponse> getCourses(Long tagId, String filterByName, Pageable pageable) {
		var coursePage = tagRepository.getCourses(Arrays.asList(new Long[] { tagId }), null, null, filterByName, pageable);
		var courses = courseConverter.fromCourseListToCourseViewResponseList(coursePage.getContent(), null);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}
	// TODO filter out non-public courses if not admin
	// TODO hasAccess
	public Page<CoursePublicViewResponse> getCourses(List<Long> tagIdsp, List<Long> tagIdsn, List<Long> courseIdsn, String filterByName, Pageable pageable) {
		var coursePage = tagRepository.getCourses(tagIdsp, tagIdsn, courseIdsn, filterByName, pageable);
		var courses = courseConverter.fromCourseListToCoursePublicViewResponseList(coursePage.getContent(), null);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}

	// TODO filter out non-public courses if not admin
	public Page<CourseViewResponse> getCoursesUnassigned(Long tagId, String filterByName, Pageable pageable) {
		var coursePage = tagRepository.getCoursesUnassigned(tagId, filterByName, pageable);
		var courses = courseConverter.fromCourseListToCourseViewResponseList(coursePage.getContent(), null);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}

	@Transactional
	public void assignCourses(Long tagId, AssignCoursesRequest coursesRequest) {
		var tagWithContent = tagWithContentRepository.findById(tagId)
				.orElseThrow(() -> new NotFoundException("tag-w-id-not-exist"));
		var courses = courseRepository.findAllById(coursesRequest.getCoursesIds());
		tagWithContent.addToCourses(courses);
	}

	@Transactional
	public void unassignCourses(Long tagId, AssignCoursesRequest coursesRequest) {
		var tagWithContent = tagWithContentRepository.findById(tagId)
				.orElseThrow(() -> new NotFoundException("tag-w-id-not-exist"));
		var courses = courseRepository.findAllById(coursesRequest.getCoursesIds());
		tagWithContent.removeFromCourses(courses);
	}

	@Transactional
	public void assignWebinars(Long tagId, AssignCoursesRequest webinarsRequest) {
		var tagWithContent = tagWithContentRepository.findById(tagId)
				.orElseThrow(() -> new NotFoundException("tag-w-id-not-exist"));
		var webinars = webinarRepository.findAllById(webinarsRequest.getCoursesIds());
		tagWithContent.addToWebinars(webinars);
	}

	@Transactional
	public void unassignWebinars(Long tagId, AssignCoursesRequest webinarsRequest) {
		var tagWithContent = tagWithContentRepository.findById(tagId)
				.orElseThrow(() -> new NotFoundException("tag-w-id-not-exist"));
		var webinars = webinarRepository.findAllById(webinarsRequest.getCoursesIds());
		tagWithContent.removeFromWebinars(webinars);
	}

	@Transactional
	public void assignLiveEvents(Long tagId, AssignCoursesRequest liveEventsRequest) {
		var tagWithContent = tagWithContentRepository.findById(tagId)
				.orElseThrow(() -> new NotFoundException("tag-w-id-not-exist"));
		var liveEvents = liveEventRepository.findAllById(liveEventsRequest.getCoursesIds());
		tagWithContent.addToLiveEvents(liveEvents);
	}

	@Transactional
	public void unassignLiveEvents(Long tagId, AssignCoursesRequest liveEventsRequest) {
		var tagWithContent = tagWithContentRepository.findById(tagId)
				.orElseThrow(() -> new NotFoundException("tag-w-id-not-exist"));
		var liveEvents = liveEventRepository.findAllById(liveEventsRequest.getCoursesIds());
		tagWithContent.removeFromLiveEvents(liveEvents);
	}
}
