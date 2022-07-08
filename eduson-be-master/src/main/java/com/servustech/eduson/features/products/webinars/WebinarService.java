package com.servustech.eduson.features.products.webinars;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.categories.modules.ModuleService;
import com.servustech.eduson.features.categories.tags.TagService;
import com.servustech.eduson.features.categories.tags.dto.TagDto;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.products.courses.CourseConverter;
import com.servustech.eduson.features.products.courses.CourseService;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import com.servustech.eduson.features.products.courses.dto.StreamDataDto;
import com.servustech.eduson.features.products.courses.dto.CourseResponse;
import com.servustech.eduson.features.products.courses.dto.CourseResponseExt;
import com.servustech.eduson.features.products.webinars.dto.WebinarDto;
import com.servustech.eduson.features.products.webinars.dto.WebinarViewResponse;
import com.servustech.eduson.features.products.webinars.dto.WebinarPublicViewResponse;
import com.servustech.eduson.features.products.webinars.dto.WebinarViewResponseExt;
import com.servustech.eduson.features.products.webinars.dto.WebinarsForCourseResponse;
import com.servustech.eduson.features.products.webinars.dto.CoursePlaceDto;
import com.servustech.eduson.features.products.courses.dto.ProductLinksDto;
import com.servustech.eduson.security.payload.StreamUsageAction;
import com.servustech.eduson.security.payload.StreamQuality;
import com.servustech.eduson.security.payload.StreamType;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.BlockingQueue;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Date;

@AllArgsConstructor
@Service
public class WebinarService {

	private final WebinarRepository webinarRepository;
	private final TagService tagService;
	private final ModuleService moduleService;
	private final WebinarsConverter webinarsConverter;
	private final UserService userService;
	private final CourseService courseService;
	private final FileService fileService;
	private final CourseConverter courseConverter;
	private final FavoriteWebinarRepository favoriteWebinarRepository;
	private final PermissionsService permissionsService;
	private ThreadPoolTaskScheduler taskScheduler;

	public WebinarViewResponseExt create(WebinarDto webinarDto, User user, MultipartFile imageFile) {

		var coordinators = userService.findAllByIds(webinarDto.getCoordinatorIds());

		var image = imageFile == null ? fileService.findById(-1L) : fileService.saveWithFile(imageFile);

		var webinar = Webinar.builder()
				.acronym(webinarDto.getAcronym())
				.name(webinarDto.getName())
				.credits(webinarDto.getCredits())
				.price(webinarDto.getPrice())
				.tags(tagService.findAllByIds(webinarDto.getTagIds()))
				.modules(moduleService.findAllByIds(webinarDto.getModuleIds()))
				.description(webinarDto.getDescription())
				.admin(user)
				.coordinators(coordinators)
				.imageFile(image)
				.startTime(webinarDto.getStartTime())
				.endTime(webinarDto.getEndTime())
				.published(webinarDto.getPublished())
				.addedDate(ZonedDateTime.now())
				.build();

		webinar = webinarRepository.save(webinar);
		schedule(webinar);
		return webinarsConverter.fromWebinarToWebinarViewResponseExt(webinar, null);
	}

	public void deleteWebinar(Long webinarId) {
		// TODO for all products: check for connections before deleting on backend also!!!
		var webinar = findById(webinarId);
		webinarRepository.delete(webinar);
	}

	public Webinar findById(Long webinarId) {
		return webinarRepository.findById(webinarId)
				.orElseThrow(() -> new NotFoundException("webinar-w-id-not-exist"));
	}

	public WebinarViewResponseExt findById2(Long webinarId, User user) {
		var webinar = webinarRepository.findById(webinarId)
				.orElseThrow(() -> new NotFoundException("webinar-w-id-not-exist")); // TODO
		if ((user == null || !user.isAdmin()) && !webinar.getPublished()) {
			throw new NotFoundException("webinar-w-id-not-exist");
		}
		return webinarsConverter.fromWebinarToWebinarViewResponseExt(webinar, user);

	}

	public Page<WebinarViewResponse> getAllWebinars(Pageable pageable, String filterByName, User user) {
		if (filterByName == null) {
			filterByName = "";
		}
		var webinarPage = user.isAdmin() ? 
			webinarRepository.findAllAndFilterByName(filterByName, pageable) :
			webinarRepository.findAllByPublishedAndFilterByName(filterByName, pageable);
		var webinars = webinarsConverter.fromWebinarListToWebinarViewResponseList(webinarPage.getContent(), user);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}

	public List<WebinarPublicViewResponse> getNewWebinars(User user) {
		Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
		Pageable pageable = PageRequest.of(0, 6, sort2);
		var webinarList = webinarRepository.findAllByPublished(pageable);
		return webinarsConverter.fromWebinarListToWebinarPublicViewResponseList(webinarList.getContent(), user);
	}

	public Page<WebinarPublicViewResponse> searchWebinars(List<Long> tagIdsp, List<Long> tagIdsn, String filterByName, Pageable pageable, User user) {
		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		var webinarPage = webinarRepository.searchBy(tagIdsp, tagIdsn, filterByName, pageable); // TODO Pageable.unpaged() ???
		return new PageImpl<>(
				webinarsConverter.fromWebinarListToWebinarPublicViewResponseList(webinarPage.getContent(), user),
				pageable, webinarPage.getTotalElements());
	}

	public Page<WebinarViewResponse> viewWebinars(String filterByName, Pageable pageable, User user) {

		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		var webinarPage = webinarRepository.findAllAndFilterByName(filterByName, pageable);

		var webinars = webinarsConverter.fromWebinarListToWebinarViewResponseList(webinarPage.getContent(), user);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}

	@Transactional
	public void assignCoursesToWebinar(Long webinarId, AssignCoursesRequest coursesRequest) {
		var webinar = findById(webinarId);
		coursesRequest.getCoursesIds().forEach(id -> {
			webinar.addCourse(courseService.findById(id), null);
		});
	}

	@Transactional
	public void unassignCoursesFromWebinar(Long webinarId, AssignCoursesRequest coursesRequest) {
		var webinar = findById(webinarId);
		var courses = courseService.findAllByIds(coursesRequest.getCoursesIds());
		courses.forEach(webinar::removeCourse);
	}

	@Transactional
	public void saveCoursesPlace(Long webinarId, CoursePlaceDto coursesPlace) {
		var webinar = findById(webinarId);
		webinar.saveCoursesPlace(coursesPlace.getOrder());
	}

	@Transactional
	public WebinarViewResponseExt updateWebinar(Long webinarId, WebinarDto request, MultipartFile imageFile) {

		var webinar = findById(webinarId);
		var startTimeOld = webinar.getStartTime();

		webinar.setName(request.getName());
		webinar.setPrice(request.getPrice());
		webinar.setTags(tagService.findAllByIds(request.getTagIds()));
		webinar.setModules(moduleService.findAllByIds(request.getModuleIds()));
		webinar.setCredits(request.getCredits());
		webinar.setAcronym(request.getAcronym());
		webinar.setDescription(request.getDescription());
		webinar.setStartTime(request.getStartTime());
		webinar.setEndTime(request.getEndTime());
		webinar.setCoordinators(userService.findAllByIds(request.getCoordinatorIds()));
		webinar.setPublished(request.getPublished());

		if (imageFile != null) {
			var imageFile2 = fileService.saveWithFile(imageFile);
			webinar.setImageFile(imageFile2);
		} else if (request.getImageFileId() != null) {
			webinar.setImageFile(fileService.findById(request.getImageFileId()));
		} else {
			webinar.setImageFile(fileService.findById(-1L));
		}

		if (webinar.getStartTime().toInstant().toEpochMilli() != startTimeOld.toInstant().toEpochMilli()) {
			schedule(webinar);
		}

		return webinarsConverter.fromWebinarToWebinarViewResponseExt(webinar, null);
	}

	public Page<CourseResponse> getAllCoursesAssigned(Long webinarId, String filterByName, Pageable pageable) {

		var coursePage = courseService.findAllByWebinar(webinarId, filterByName, pageable);

		List<CourseResponse> courseResponses = new ArrayList<>();
		var courses = coursePage.getContent();
		courses.forEach(webinarCourse -> {
			var response = courseConverter.toCourseResponse(webinarCourse);
			courseResponses.add(response);
		});

		return new PageImpl<>(courseResponses, pageable,
				coursePage.getTotalElements());

	}

	public Page<CourseResponse> getAllCoursesUnassigned(Long webinarId, String filterByName, Pageable pageable) {

		var coursePage = courseService.findAllNotInWebinar(webinarId, filterByName, pageable);

		List<CourseResponse> courseResponses = new ArrayList<>();
		var courses = coursePage.getContent();
		courses.forEach(course -> {
			var response = courseConverter.toCourseResponse2(course, false, null);
			courseResponses.add(response);
		});

		return new PageImpl<>(courseResponses, pageable,
				coursePage.getTotalElements());

	}

	public List<CourseResponseExt> getAllCoursesExt(Long webinarId, User user) {

		var webinar = findById(webinarId);
		// var courses = webinar.getCourses();
		var courses = courseService.findAllByWebinar(webinarId, null, Pageable.unpaged()).getContent();

		List<CourseResponseExt> courseResponses = new ArrayList<>();

		courses.forEach(course -> {
			var response = courseConverter.toCourseResponseExt(course, user);
			courseResponses.add(response);
		});

		return courseResponses;
	}

	public List<Webinar> findAll() {
		return webinarRepository.findAll();
	}

	public Page<WebinarViewResponse> getFavoritedWebinars(Pageable pageable, String filterByName, User user) {
		if (filterByName == null) {
			filterByName = "";
		}
		var webinarPage = favoriteWebinarRepository.findAllByUserAndWebinarNameContainsOrderByWebinarAddedDateDesc(user, filterByName, pageable);
		var webinars = webinarsConverter.fromWebinarListToWebinarViewResponseList(
				webinarPage.getContent().stream().map(fw -> fw.getWebinar()).collect(Collectors.toList()), user);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}

	public Page<WebinarViewResponse> getMyWebinars(Pageable pageable, String filterByName, User user) {
		// pageableeee? // it seems that we have to treat the library like the favorites
		// in the database. those should be "views".
		// this time we use the current methods.
		// TODO we should invent something in order not to do the same query twice
		if (filterByName == null) {
			filterByName = "";
		}
		List<Webinar4Check> allWebinarsChecked = permissionsService.getWebinars(user, filterByName);
		Page<Webinar> webinarPage = webinarRepository
				.findByIdIn(allWebinarsChecked.stream().map(w -> w.getId()).collect(Collectors.toList()), pageable);

		var webinars = webinarsConverter.fromWebinarListToWebinarViewResponseList(
				webinarPage.getContent(), user);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}
	public Page<WebinarPublicViewResponse> getMyUpcomingWebinars(Pageable pageable, String filterByName, User user) { // TODO we might use a narrower WebinarViewResponse
		// almost the same as the previous. except that it's public view this time, as search result dto asks for this.
		List<Webinar4Check> allWebinarsChecked = permissionsService.getWebinars(user, filterByName);
		Page<Webinar> webinarPage = webinarRepository
				.findByIdInAndEndTimeGreaterThanOrderByStartTimeAsc(allWebinarsChecked.stream().map(le -> le.getId()).collect(Collectors.toList()), ZonedDateTime.now(), pageable);
		var webinars = webinarsConverter.fromWebinarListToWebinarPublicViewResponseList(
			webinarPage.getContent(), user);
		return new PageImpl<>(webinars, pageable, webinarPage.getTotalElements());
	}

	public ProductLinksDto hasLinks(Long id) {
    Pageable pageable = PageRequest.of(0, 1, JpaSort.unsorted());
		return ProductLinksDto.builder()
			.courses(webinarRepository.findFirstCourse(id, pageable).size() > 0)
			.modules(webinarRepository.findFirstModule(id, pageable).size() > 0)
			.subscribers(webinarRepository.findFirstPermission(id, pageable).size() > 0)
			.subscriptions(webinarRepository.findFirstSubscription(id, pageable).size() > 0)
		.build();
	}

	public List<TagDto> getCoursesTags(AssignCoursesRequest coursesRequest) {
		return courseService.getCoursesTags(coursesRequest.getCoursesIds());
	}

	public boolean stopLive(Long id) {
		return courseService.stopLive("w", id);
	}

	public String stream(Long id, Boolean live) {
		var coursePage = courseService.findAllByWebinar(id, null, Pageable.unpaged());
		var courses = coursePage.getContent();
		var size = courses.size();
		var index = 0L;
		courseService.clearDirectory("w", id, live);
		Iterator<WebinarCourse> iterator = courses.iterator();
		boolean isLast = false;
    while (iterator.hasNext()) {
        WebinarCourse course = iterator.next();
        if (!iterator.hasNext()) {
            //last
					isLast = true;
        }
				if (live) {
					if (!courseService.isLive("w", null, id)) { // canceled
						// do not start further streams:
						return "";
					}
					// set index:
					courseService.setLiveIndex("w", id, index, course.getCourse().getId());
				}
				courseService.stream(course.getCourse().getId(), 0d, null, live, "w", id, isLast, index);
				index++;
		}
		if (!live) {
			checkManifestFile(StreamType.vod, id);
		}
		return "";
	}

	public StreamDataDto getLiveData(Long id) {
		return courseService.getLiveData("w", id);
	}
	public StreamDataDto getVodData(Long id, Long cc) {
		var coursePage = courseService.findAllByWebinar(id, null, Pageable.unpaged());
		var courses = coursePage.getContent();
		var size = courses.size();
		var index = 0L;
		var discontinuitiesSoFar = 0L;
		Iterator<WebinarCourse> iterator = courses.iterator();
    while (iterator.hasNext()) {
			WebinarCourse course = iterator.next();
			var cnt = courseService.countOfDiscontinuities(course.getCourse());
			discontinuitiesSoFar += cnt;
			if (cc <= discontinuitiesSoFar) {
				return courseService.getDiscontinuity(course.getCourse(), cc - (discontinuitiesSoFar - cnt), index, id);
			}
			index++;
		}
		return new StreamDataDto();
	}
	public void checkManifestFile(StreamType stream, Long id) {
		courseService.checkManifestFile(stream, "w", id, this);
	}
	public void registerUsage(User user, Long webinarId, StreamType type, StreamUsageAction action, Long chunkNum, StreamQuality quality) {
		courseService.registerUsage(user, webinarId, null, type, action, chunkNum, quality);
	}

	@Transactional
	public void togglePublish(Long webinarId) {
		var webinar  = findById(webinarId);
		var published  = webinar.getPublished();
		webinar.setPublished(!published);
	}
	@Transactional
	public void publish(Long webinarId) {
		var webinar  = findById(webinarId);
		webinar.setPublished(true);
	}
	@Transactional
	public void unpublish(Long webinarId) {
		var webinar  = findById(webinarId);
		webinar.setPublished(false);
	}

	public List<Webinar> getWebinarsToSchedule() {
		return webinarRepository.findByStartTimeGreaterThanOrderByStartTimeAsc(ZonedDateTime.now());
	}
	// private Object[] getScheduledTasks() {
	// 	ThreadPoolTaskScheduler xScheduler = (ThreadPoolTaskScheduler)this.taskScheduler;
  //   ScheduledThreadPoolExecutor xService = (ScheduledThreadPoolExecutor)xScheduler.getScheduledThreadPoolExecutor();
  //   BlockingQueue<Runnable> queue = xService.getQueue();
  //   Object[] scheduledJobs = queue.toArray();
	// }
	// private RunnableWebinar getScheduledTask(Long id) {
	// 	Object[] scheduledJobs = getScheduledTasks();
	// 	for (int i = 0; i < scheduledJobs.length; i++) {
	// 		if (scheduledJobs[i] instanceof RunnableWebinar) {
	// 			RunnableWebinar rw = (RunnableWebinar) scheduledJobs[i];
	// 			if (rw.getId().equals(id)) {
	// 				return rw;
	// 			}
	// 		}
	// 	}
	// 	return null;
	// }
	public void setTaskScheduler(ThreadPoolTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}
	private void schedule(Webinar w) {
		// check if rescheduling:
		// RunnableWebinar task = getScheduledTask(id);
		// if (task != null) {
		// 	task.cancel();
		// }
		taskScheduler.schedule(new RunnableWebinar(
			w.getName(), w.getStartTime(), w.getId(), this
		), new Date(w.getStartTime().toInstant().toEpochMilli()));
	}
}
