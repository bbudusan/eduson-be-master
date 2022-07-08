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
import com.servustech.eduson.features.products.courses.dto.GetUsageParams;
import com.servustech.eduson.features.products.courses.dto.ChunkUsageResponse;
import com.servustech.eduson.features.products.courses.dto.CourseDto;
import com.servustech.eduson.features.products.courses.dto.CourseViewResponse;
import com.servustech.eduson.features.products.courses.dto.AdvertDto;
import com.servustech.eduson.features.products.courses.dto.StreamDataDto;
import com.servustech.eduson.features.products.courses.dto.CoursePublicViewResponse;
import com.servustech.eduson.features.products.courses.dto.CourseViewResponseExt;
import com.servustech.eduson.features.products.courses.dto.CoursesView;
import com.servustech.eduson.features.products.courses.dto.ProductLinksDto;
import com.servustech.eduson.features.products.webinars.WebinarsConverter;
import com.servustech.eduson.features.products.webinars.dto.WebinarsForCourseResponse;
import com.servustech.eduson.security.payload.StreamQuality;
import com.servustech.eduson.features.products.webinars.WebinarCourse;
import com.servustech.eduson.features.products.webinars.WebinarService;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import com.servustech.eduson.exceptions.CustomException;
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
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Iterator;

import java.util.function.Consumer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
// import java.io.OutputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Executors;
import java.util.Hashtable;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.ProcessBuilder;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.MutablePair;

import java.time.LocalTime;
@AllArgsConstructor
@Service
public class CourseService {

	private final CourseRepository courseRepository;
	private final TagService tagService;
	private final ModuleService moduleService;
	private final UserService userService;
	private final AdvertService advertService;
	private final CourseConverter courseConverter;
	private final FileService fileService;
	private final WebinarsConverter webinarsConverter;
	private final FavoriteCourseRepository favoriteCourseRepository;
	private final PermissionsService permissionsService;
	private final AdvertConverter advertConverter;
	private final ChunkRepository chunkRepository;
	private final ChunkUsageRepository chunkUsageRepository;
	@Autowired
	private final Hashtable<Long, MutablePair<MutablePair<Process,Process>,Advert>> courseProcesses;
	@Autowired
	private final Hashtable<Long, MutableTriple<MutablePair<Process,Process>,MutablePair<Long,Long>,Advert>> webinarProcesses;// pair: index, courseId
	// private static Long currentIndex;
	// private static Advert currentAdvert;
	// private static Long currentId;
	// private static Long currentWebinarId;

	public CourseViewResponseExt create(CourseDto courseDto, User user, MultipartFile imageFile,
			MultipartFile courseFile) {
		var lector = userService.findById(courseDto.getLectorId());

		var imageFile2 = imageFile == null ? fileService.findById(-1L) : fileService.saveWithFile(imageFile);
		var courseFile2 = courseFile == null ? fileService.findById(-1L) : fileService.saveWithFile(courseFile);

		var course = Course.builder()
				.name(courseDto.getName())
				.description(courseDto.getDescription())
				.imageFile(imageFile2)
				.courseFile(courseFile2)
				.price(courseDto.getPrice())
				.tags(tagService.findAllByIds(courseDto.getTagIds()))
				.modules(moduleService.findAllByIds(courseDto.getModuleIds()))
				.addedDate(ZonedDateTime.now())
				.admin(user)
				.lector(lector)
				.duration(courseDto.getDuration())
				// https://stackoverflow.com/questions/58044657/convert-double-to-java-sql-time java.sql.Time must be converted to LocalTime!!
				.publishedDate(courseDto.getPublishedDate())
				.published(courseDto.getPublished())
				.build();

		courseRepository.save(course);
		return courseConverter.fromCourseToCourseViewResponseExt(course, null);
	}

	public void deleteCourse(Long courseId) {
		var course = findById(courseId);
		courseRepository.delete(course);
	}

	public Course findById(Long courseId) {
		return courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("course-w-id-not-exist"));
	}

	public CourseViewResponseExt findById2(Long courseId, User user) {
		var course = courseRepository.findById(courseId)
				.orElseThrow(() -> new NotFoundException("course-w-id-not-exist")); // TODO
		var publishedDate = course.getPublishedDate();
		if ((user == null || !user.isAdmin()) && (publishedDate != null && ZonedDateTime.now().isBefore(publishedDate) || !course.getPublished() ) ) {
			throw new NotFoundException("course-w-id-not-exist"); // TODO why is throwing 500, why not 404?
		}
		return courseConverter.fromCourseToCourseViewResponseExt(course, user);
	}

	// TODO filter out non-public courses if not admin
	// like this
	public Page<CourseViewResponse> getAllCoursesPublic(Pageable pageable, String filterByName, User user) {
		if (filterByName == null) {
			filterByName = "";
		}
		var coursePage = courseRepository.findAllByIPublishedDateIsNullOrPublishedDateBeforeIAndNameContains(ZonedDateTime.now(), filterByName, pageable);
		var courses = courseConverter.fromCourseListToCourseViewResponseList(coursePage.getContent(), user);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}
	// public Page<CourseViewResponse> getAllCoursesAdmin(Pageable pageable, User user) {
	// 	var coursePage = courseRepository.findAll(pageable);
	// 	var courses = courseConverter.fromCourseListToCourseViewResponseList(coursePage.getContent(), user);
	// 	return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	// }

	public List<CoursePublicViewResponse> getNewCourses(User user) {
		Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
		Pageable pageable = PageRequest.of(0, 6, sort2); // findTop3ByAge() TODO Top6
		var courseList = courseRepository.findAllByPublishedDateIsNullOrPublishedDateBefore(ZonedDateTime.now(), pageable);
		return courseConverter.fromCourseListToCoursePublicViewResponseList(courseList.getContent(), user);
	}

	public Page<CoursePublicViewResponse> searchCourses(List<Long> tagIdsp, List<Long> tagIdsn, String filterByName, Pageable pageable, User user) {
		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		var coursePage = courseRepository.searchBy(tagIdsp, tagIdsn, filterByName, pageable); // TODO Pageable.unpaged() ???
		return new PageImpl<>(courseConverter.fromCourseListToCoursePublicViewResponseList(coursePage.getContent(), user),
				pageable, coursePage.getTotalElements());
	}

	public Page<WebinarCourse> findAllByWebinar(Long webinarId, String filterByName, Pageable pageable) {
		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			if (order.getProperty().equals("lector")) {
				Sort sort2 = JpaSort.unsafe(order.getDirection(),
						"CONCAT(wc.course.lector.firstName, ' ', wc.course.lector.lastName)"); // TODO aggregate
				pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2); // TODO at end of foreach
			} else {
				Sort sort2 = JpaSort.unsafe(order.getDirection(), "wc.course." + order.getProperty()); // TODO aggregate
				pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2); // TODO at end of foreach
			}
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.ASC, "wc.place").andUnsafe(Sort.Direction.DESC, "wc.course.addedDate");// wc.place
																																																												// ASC,
																																																												// wc.course.addedDate
																																																												// DESC
			pageable = PageRequest.of(pageable == Pageable.unpaged() ? 0 : pageable.getPageNumber(), pageable == Pageable.unpaged() ? 100000 : pageable.getPageSize(), sort2);
		}
		return courseRepository.findCoursesByWebinarAndFilterByName(webinarId, filterByName, pageable);
	}

	public Page<Course> findAllNotInWebinar(Long webinarId, String filterByName, Pageable pageable) {
		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			if (order.getProperty().equals("lector")) {
				Sort sort2 = JpaSort.unsafe(order.getDirection(), "CONCAT(c.lector.firstName, ' ', c.lector.lastName)");
				pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
			}
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		return courseRepository.findCoursesNotInWebinarAndFilterByName(webinarId, filterByName, pageable);
	}

	// TODO filter out non-public courses if not admin
	// maybe this is only for admin
	public Page<CourseViewResponse> viewCourses(String filterByName, Pageable pageable, User user) {

		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			if (order.getProperty().equals("lector")) {
				Sort sort2 = JpaSort.unsafe(order.getDirection(), "CONCAT(c.lector.firstName, ' ', c.lector.lastName)");
				pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
			}
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		var coursePage = courseRepository.findAllAndFilterByName(filterByName, pageable);

		var courses = courseConverter.fromCourseListToCourseViewResponseList(coursePage.getContent(), user);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}

	public Page<CoursesView> viewCoursesByLector(Long userId, String filterByName, User user, Pageable pageable) {
		var coursePage = courseRepository.findCoursesByLectorAndFilterByName(ZonedDateTime.now(), userId, filterByName, pageable);
		var courses = courseConverter.fromCourseToCoursesByLectorsViewResponseList(coursePage.getContent(), user);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}

	public List<Course> findAllByIds(List<Long> ids) {
		return courseRepository.findAllById(ids);
	}

	@Transactional
	public CourseViewResponseExt updateCourse(Long courseId, CourseDto request, MultipartFile imageFile,
			MultipartFile courseFile) {
		var course = findById(courseId);

		course.setName(request.getName());
		course.setPrice(request.getPrice());
		course.setTags(tagService.findAllByIds(request.getTagIds()));
		course.setModules(moduleService.findAllByIds(request.getModuleIds()));
		course.setDescription(request.getDescription());
		course.setLector(userService.findById(request.getLectorId()));
		course.setDuration(request.getDuration());
		course.setPublishedDate(request.getPublishedDate());
		course.setPublished(request.getPublished());

		if (imageFile != null) {
			var imageFile2 = fileService.saveWithFile(imageFile);
			course.setImageFile(imageFile2);
		} else if (request.getImageFileId() != null) {
			course.setImageFile(fileService.findById(request.getImageFileId()));
		} else {
			course.setImageFile(fileService.findById(-1L));
		}

		if (courseFile != null) {
			var courseFile2 = fileService.saveWithFile(courseFile);
			course.setCourseFile(courseFile2);
		} else if (request.getFileId() != null) {
			course.setCourseFile(fileService.findById(request.getFileId()));
		} else {
			course.setCourseFile(fileService.findById(-1L));
		}
		return courseConverter.fromCourseToCourseViewResponseExt(course, null);

	}

	public List<WebinarsForCourseResponse> getWebinarsForCourses(Long courseId) {
		var course = findById(courseId);
		var webinars = course.getWebinars();
		List<WebinarsForCourseResponse> webinarsForCourseResponses = new ArrayList<>();
		webinars.forEach(webinar -> {
			var response = webinarsConverter.toWebinarsForCourseResponse(webinar);
			webinarsForCourseResponses.add(response);
		});
		return webinarsForCourseResponses;
	}

	public List<Course> findAll() {
		return courseRepository.findAll();
	}

	// TODO filter out non-public courses if not admin
	public Page<CourseViewResponse> getFavoritedCourses(Pageable pageable, String filterByName, User user) {
		// var coursePage = user.getFavoritedCourses(pageable);
		if (filterByName == null) {
			filterByName = "";
		}
		var coursePage = favoriteCourseRepository.findAllByUserAndCourseNameContainsOrderByCourseAddedDateDesc(user, filterByName, pageable);
		var courses = courseConverter.fromCourseListToCourseViewResponseList(
				coursePage.getContent().stream().map(fc -> fc.getCourse()).collect(Collectors.toList()), user);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}

	// TODO filter out non-public courses if not admin
	// maybe we should show it even if become unpublished over time TODO
	public Page<CourseViewResponse> getMyCourses(Pageable pageable, String filterByName, User user) {
		if (filterByName == null) {
			filterByName = "";
		}
		List<Course4Check> allCoursesChecked = permissionsService.getCourses(user, filterByName);
		Page<Course> coursePage = courseRepository
				.findByIdIn(allCoursesChecked.stream().map(w -> w.getId()).collect(Collectors.toList()), pageable);

		var courses = courseConverter.fromCourseListToCourseViewResponseList(
				coursePage.getContent(), user);
		return new PageImpl<>(courses, pageable, coursePage.getTotalElements());
	}

	public ProductLinksDto hasLinks(Long id) {
    Pageable pageable = PageRequest.of(0, 1, JpaSort.unsorted());
		return ProductLinksDto.builder()
			.webinars(courseRepository.findFirstWebinar(id, pageable).size() > 0)
			.modules(courseRepository.findFirstModule(id, pageable).size() > 0)
			.subscribers(courseRepository.findFirstPermission(id, pageable).size() > 0)
			.subscriptions(courseRepository.findFirstSubscription(id, pageable).size() > 0)
		.build();
	}

	public List<TagDto> getCoursesTags(List<Long> ids) {
		List<Tag> tags = courseRepository.findDistinctTagsByIdIn(ids);
		return tags.stream().map(tag -> TagDto.builder()
			.name(tag.getName())
			.tagCategoryId(tag.getTagCategory().getId())
			.id(tag.getId())
			.build()
		).collect(Collectors.toList());
	}

	public Page<AdvertDto> getAllAdvertsAssigned(Long courseId, String filterByName, Pageable pageable) {

		var advertPage = advertService.findAllByCourse(courseId, filterByName, pageable);

		List<AdvertDto> advertResponses = new ArrayList<>();
		var adverts = advertPage.getContent();
		adverts.forEach(courseAdvert -> {
			var response = advertConverter.toAdvertDto(courseAdvert);
			advertResponses.add(response);
		});

		return new PageImpl<>(advertResponses, pageable,
				advertPage.getTotalElements());
	}

	public Page<AdvertDto> getAllAdvertsUnassigned(Long courseId, String filterByName, Pageable pageable) {

		var advertPage = advertService.findAllNotInCourse(courseId, filterByName, pageable);

		List<AdvertDto> advertResponses = new ArrayList<>();
		var adverts = advertPage.getContent();
		adverts.forEach(advert -> {
			var response = advertConverter.toAdvertDto2(advert, false, null, null, null);
			advertResponses.add(response);
		});

		return new PageImpl<>(advertResponses, pageable,
				advertPage.getTotalElements());
	}

	@Transactional
	public void assignAdvertsToCourse(Long courseId, AssignCoursesRequest coursesRequest) {
		var course = findById(courseId);
		coursesRequest.getCoursesIds().forEach(id -> {
			course.addAdvert(advertService.findById(id), null, null, 2L);
		});
	}

	@Transactional
	public void unassignAdvertsFromCourse(Long courseId, AssignCoursesRequest coursesRequest) {
		var course = findById(courseId);
		var adverts = advertService.findAllByIds(coursesRequest.getCoursesIds());
		adverts.forEach(course::removeAdvert);
	}

	@Transactional
	public void updateAdvert(Long courseId, AdvertDto advertDto) {
		var course = findById(courseId);
		var advert = advertService.findById(advertDto.getId());
		course.updateAdvert(advert, advertDto.getStart(), advertDto.getPriority(), advertDto.getRule());
	}

	public void clearDirectory2(String type, Long id, Boolean live) {
		// TODO invoke this after a live exits normally, after a while
		try {
			System.out.println("clear directory " + (live ? "live/":"vod/") + type + id);
			ProcessBuilder builder = new ProcessBuilder();
			builder.inheritIO();
			System.out.println("building the rm command...");
			builder.command(
				"rm",  "-r",  "--interactive=never", "/home/ubuntu/hls/"+(live ? "live" : "vod")+"/"+type+id.toString()
			);
			System.out.println("starting rm...");
			Process process = builder.start();
			System.out.println("waiting rm...");
			int exitCode = process.waitFor();
			System.out.println("done rm " + exitCode);
		}	catch (Exception e) {
			System.out.println("cleardirectory2 problem " + e);
			e.printStackTrace();
			throw new CustomException("system-problem");
		}

	}
	@Transactional
	public void clearDirectory(String type, Long id, Boolean live) {
		var chunks = type.equals("w") ? chunkRepository.findAllByWebinarIdAndType(id, live ? StreamType.live : StreamType.vod) : chunkRepository.findAllByCourseIdAndTypeAndWebinarIdIsNull(id, live ? StreamType.live : StreamType.vod);
		chunks.forEach(chunk -> chunk.setActive(false));
		try {
			clearDirectory2(type, id, live);
			if (live) {
				if (isLive(type, id, id)) {
					System.out.println("stopping previous live of " + id);
					stopLive(type, id);

				}
			}
			ProcessBuilder builder = new ProcessBuilder();
			builder.inheritIO();
			System.out.println("building the mkdir command...");
			builder.command(
				"mkdir",  "-p",  "/home/ubuntu/hls/"+(live ? "live" : "vod")+"/"+type+id.toString()
			);
			System.out.println("starting mkdir...");
			Process process = builder.start();
			System.out.println("waiting mkdir...");
			int exitCode = process.waitFor();
			System.out.println("done mkdir " + exitCode);

			builder = new ProcessBuilder();
			builder.inheritIO();
			System.out.println("building the touch command...");
			builder.command(
				"touch",  "/home/ubuntu/hls/"+(live ? "live" : "vod")+"/"+type+id.toString()+"/"+"stream.m3u8"
			);
			System.out.println("starting touch...");
			process = builder.start();
			System.out.println("waiting touch...");
			exitCode = process.waitFor();
			System.out.println("done touch " + exitCode);

			if (live) {
				setLive(type, id);
				System.out.println("building the watcher command...");
				ProcessBuilder[] builders = {
					new ProcessBuilder("inotifywait",
						"--monitor",
						"--format",
						"\"%w\"",
						"--event",
						"moved_from",
						"/home/ubuntu/hls/"+(live ? "live" : "vod")+"/"+type+id.toString()
					),
					new ProcessBuilder(
						"xargs",
						"-n1",
						"curl",
						// "-d",
						// (live ? "live" : "vod") + "/"+type+id.toString(),
						"http://localhost:8080/api/"+(type.equals("w")?"webinar":"course")+"/newchunk/"+(live ? "live" : "vod")+"/"+id.toString()
					)
				};
				// builders.forEach(b->b.inheritIO());
				System.out.println("starting watcher pipeline...");
				List<Process> processes = ProcessBuilder.startPipeline(
					Arrays.asList(builders));
				Process first = processes.get(0);
				setLiveProcess(type, id, first, true);
			}
		}	catch (Exception e) {
			System.out.println("cleardirectory problem " + e);
			e.printStackTrace();
			throw new CustomException("system-problem");
		}
	}

	public boolean stopLive(String type, Long id) {
		clearDirectory2(type, id, true);
		// delete the entry and then stop the process and avoid race condition somehow :(
		MutablePair<Process,Process> processes = null;
		if (type.equals("w")) {
			var entry = webinarProcesses.get(id);
			if (entry == null) {
				return false;
			}
			processes = entry.getLeft();
			webinarProcesses.remove(id);
		} else {
			var entry = courseProcesses.get(id);
			if (entry == null) {
				return false;
			}
			processes = entry.getLeft();
			courseProcesses.remove(id);
		}
		if (processes != null) {
			var process = processes.getLeft();
			if (process != null) {
				System.out.println("destroying watcher...");
				process.destroy();				
			}
			process = processes.getRight();
			if (process != null) {
				System.out.println("destroying...");
				process.destroy();
				try {
				Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	private void setLiveProcess(String type, Long id, Process process, boolean isWatcher) {
		try {
			MutablePair<Process,Process> processes = null;			
			if (type.equals("w")) {
				var entry = webinarProcesses.get(id);
				if (entry == null) {
					return;
				}
				processes = entry.getLeft();
			} else {
				var entry = courseProcesses.get(id);
				if (entry == null) {
					return;
				}
				processes = entry.getLeft();
			}
			if (isWatcher) {
				processes.setLeft(process);
			} else {
				processes.setRight(process);
			}
		} catch (Exception e) { // TODO when element is not found in the hash table and race conditions :(
			if (process != null) {
				process.destroy();
			}
		}
	}

	public boolean isLive(String type, Long idc, Long idw) {
		if (type.equals("w")) {
			return webinarProcesses.containsKey(idw);
		} else {
			return courseProcesses.containsKey(idc);
		}
	}

	public void setLive(String type, Long id) {
		if (type.equals("w")) {
			if (!webinarProcesses.containsKey(id)) {
				webinarProcesses.put(id, MutableTriple.of(MutablePair.of(null, null), MutablePair.of(null, null), null));
			}
		} else {
			if (!courseProcesses.containsKey(id)) {
				courseProcesses.put(id, MutablePair.of(MutablePair.of(null, null), null));
			}
		}
	}

	public void setLiveIndex(String type, Long webinarId, Long index, long courseId) {
		try {
			if (type.equals("w")) {
				var entry = webinarProcesses.get(webinarId);
				if (entry == null) {
					return;
				}
				entry.setMiddle(MutablePair.of(index, courseId));
			} else {
				// do nothing
				// var entry = courseProcesses.get(courseId);
			}
		} catch (Exception e) {

		}
	}

	public void setLiveAdvert(String type, Long idc, Long idw, Advert advert) {
		try {
			if (type.equals("w")) {
				var entry = webinarProcesses.get(idw);
				if (entry == null) {
					return;
				}
				entry.setRight(advert);
			} else {
				var entry = courseProcesses.get(idc);
				if (entry == null) {
					return;
				}
				entry.setRight(advert);
			}
		} catch (Exception e) {
		}
	}

	private void ffmpeg(String filename, String title, Double start, Double end, Boolean live, String type, Long id, boolean isLast) {
		System.out.println("ffmpeg " + filename + " between " + start + " and " + end + " in " + (live ? "live/":"vod/") + type + id);
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.inheritIO();
			System.out.println("building the command...");
			java.util.List<java.lang.String> commandline = new ArrayList<>();
			commandline.add("/home/ubuntu/ffmpeg/ffmpeg-5.0.1-amd64-static/ffmpeg");
			if (live) commandline.add("-re");
			commandline.add("-i");
			commandline.add("/home/ubuntu/minio/data/eduson-test/" + filename);
			if (start != 0) {
				commandline.add("-ss");
				commandline.add(start.toString());
			}
			if (end != null) {
				commandline.add("-to");
				commandline.add(end.toString());
			}
			commandline.add("-c:v");
			commandline.add("copy");
			commandline.add("-c:a");
			commandline.add("copy");
			commandline.add("-metadata");
			commandline.add("title=\""+title+"\"");
			 
			commandline.add("-f");
			commandline.add("hls");
			commandline.add("-bufsize");
			commandline.add("1835k");
			commandline.add("-pix_fmt");
			commandline.add("yuv420p");
			commandline.add("-flags");
			commandline.add("-global_header");
			commandline.add("-hls_list_size");
			if (live) {
				commandline.add("5");
			} else {
				commandline.add("0");
			}
			commandline.add("-hls_time");
			if (live) {
				commandline.add("2");
			} else {
				commandline.add("10");
			}
			commandline.add("-hls_flags");
			commandline.add("append_list" + (live ? "+delete_segments" : "")+(isLast ? "" : "+omit_endlist")+"+discont_start");
			commandline.add("-start_number");
			commandline.add("1");
			commandline.add("/home/ubuntu/hls/"+(live ? "live/":"vod/")+type+id.toString()+"/stream.m3u8");
			builder.command(commandline);
			System.out.println("starting...");
			Process process = builder.start();
			System.out.println("waiting...");
			if (live) {
				setLiveProcess(type, id, process, false);
				// runningProcess = process;
			}
			int exitCode = process.waitFor();
			System.out.println("done.");
			if (live) {
				setLiveProcess(type, id, null, false);
				// runningProcess = null;
			}
			// assert exitCode == 0;
		}	catch (Exception e) {
			System.out.println("streaming problem " + e);
			e.printStackTrace();
			throw new CustomException("streaming-problem");
		}
	}
	public String stream(Long id, Double start, Double end, Boolean live, String type, Long id2, boolean isLast, Long index) {
		if (live) {
			isLast = false;
		}
			Course course = findById(id);
			if (live) {
				// setLiveIndex(type, id2, index, id); // maybe this is not needed
			}
			if (course.getAdverts().size() > 0) {
				Iterator<CourseAdvert> iterator = course.getAdverts().iterator();
				boolean isLastAdvert = false;
				Double time = start;
				while (iterator.hasNext()) {
					CourseAdvert courseAdvert = iterator.next();
					Advert advert = courseAdvert.getAdvert();
					if (!iterator.hasNext()) {
							//last
						isLastAdvert = true;
					}
					Double advertStart = new Integer(courseAdvert.getStart().getSeconds()).doubleValue();
					Double courseDuration = new Integer(course.getDuration().getSeconds()).doubleValue();
					if (advertStart > time) {
						ffmpeg(course.getCourseFile().getPath(), course.getName(), time, advertStart, live, type, id2 == null ? id: id2, false);
						if (live) {
							if (!isLive(type, id, id2)) { // canceled
								return "";
							}
						}	
					}
					if (live) {
						setLiveAdvert(type, id, id2, advert);
					}
					// currentAdvert = advert;
					ffmpeg(advert.getFile().getPath(), advert.getName(), 0d, null, live, type, id2 == null ? id: id2, isLast && isLastAdvert && advertStart >= courseDuration);
					// currentAdvert = null;
					if (live) {
						if (!isLive(type, id, id2)) { // canceled
							return "";
						}
						setLiveAdvert(type, id, id2, null);
					}
					time = advertStart;
					if (isLastAdvert && advertStart < courseDuration) {
						ffmpeg(course.getCourseFile().getPath(), course.getName(), advertStart, end, live, type, id2 == null ? id: id2, isLast);
						if (live) {
							if (!isLive(type, id, id2)) { // canceled
								return "";
							}
						}
					}
				}		
			} else {
				ffmpeg(course.getCourseFile().getPath(), course.getName(), start, end, live, type, id2 == null ? id: id2, isLast);
			}
			if (type.equals("c")) {
				if (!live) {
					checkManifestFile(StreamType.vod, "c", id, null);
				}
			}
			return "";
	}
	public Long countOfDiscontinuities(Course course) {
		Long ret = 0L;
		if (course.getAdverts().size() > 0) {
			Iterator<CourseAdvert> iterator = course.getAdverts().iterator();
			boolean isLastAdvert = false;
			Double time = 0d;
			while (iterator.hasNext()) {
				CourseAdvert courseAdvert = iterator.next();
				Advert advert = courseAdvert.getAdvert();
				if (!iterator.hasNext()) {
						//last
					isLastAdvert = true;
				}
				Double advertStart = new Integer(courseAdvert.getStart().getSeconds()).doubleValue();
				Double courseDuration = new Integer(course.getDuration().getSeconds()).doubleValue();
				if (advertStart > time) {
					ret++;
				}
				ret++;
				time = advertStart;
				if (isLastAdvert && advertStart < courseDuration) {
					ret++;
				}
			}		
		} else {
			ret++;
		}
		return ret;
	}
	public StreamDataDto getLiveData(String type, Long id) {
		if (type.equals("w")) {
			var processEntry = webinarProcesses.get(id);
			return makeStreamDataDto(
				findById(processEntry.getMiddle().getRight()), // current course
				processEntry.getRight(), // advert
				processEntry.getMiddle().getLeft(), // current index
				id); // webinar id
		} else {
			var processEntry = courseProcesses.get(id);
			return makeStreamDataDto(
				findById(id), // current course
				processEntry.getRight(), // advert
				0L, // current index
				null); // webinar id
		}
		// return makeStreamDataDto(findById(currentId), currentAdvert, currentIndex, currentWebinarId);
	}
	public StreamDataDto getVodData(Long id, Long cc) {
		return getDiscontinuity(findById(id), cc, 0L, null);
	}
	private StreamDataDto makeStreamDataDto(Course course, Advert advert, Long index, Long webinarId) {
		return StreamDataDto.builder()
			.index(index)
			.title(course.getName())
			.id(course.getId())
			.webinarId(webinarId)
			.advertId(advert == null ? null : advert.getId())
			.advertTitle(advert == null ? null : advert.getName())
			.advertDescription(advert == null ? null : advert.getDescription())
			.advertOnClick(advert == null ? null : advert.getOnclick())
			.build();
	}
	public StreamDataDto getDiscontinuity(Course course, Long offset, Long index, Long webinarId) {
		if (course.getAdverts().size() > 0) {
			Iterator<CourseAdvert> iterator = course.getAdverts().iterator();
			boolean isLastAdvert = false;
			Double time = 0d;
			while (iterator.hasNext()) {
				CourseAdvert courseAdvert = iterator.next();
				Advert advert = courseAdvert.getAdvert();
				if (!iterator.hasNext()) {
						//last
					isLastAdvert = true;
				}
				Double advertStart = new Integer(courseAdvert.getStart().getSeconds()).doubleValue();
				Double courseDuration = new Integer(course.getDuration().getSeconds()).doubleValue();
				if (advertStart > time) {
					offset--;
					if (offset == 0) {
						return makeStreamDataDto(course, null, index, webinarId);
					}
				}
				offset--;
				if (offset == 0) {
					return makeStreamDataDto(course, advert, index, webinarId);
				}
				time = advertStart;
				if (isLastAdvert && advertStart < courseDuration) {
					offset--;
					if (offset == 0) {
						return makeStreamDataDto(course, null, index, webinarId);
					}
				}
			}
		} else {
			offset--;
			if (offset == 0) {
				return makeStreamDataDto(course, null, index, webinarId);
			}
		}
		return new StreamDataDto();
	}

	@Transactional
	public void checkManifestFile(StreamType stream, String type, Long id, WebinarService webinarService) {
		var now  = ZonedDateTime.now();
		var then = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, now.getZone());
		var thenL = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
		ZoneOffset zoneOffset = then.getZone().getRules().getOffset(thenL);
		var maxNum = type.equals("w")
			? chunkRepository.findMaxNumberByWebinarIdAndTypeAndActiveIsTrue(id, stream)
			: chunkRepository.findMaxNumberByCourseIdAndTypeAndActiveIsTrueAndWebinarIdIsNull(id, stream);
		if (maxNum == null) {
			maxNum = 0L;
		}
		File manifest = new File("/home/ubuntu/hls/"+stream+"/"+type+id.toString()+"/stream.m3u8");
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(manifest);
		} catch (java.io.FileNotFoundException e) {
			System.out.println("manifest file not found: " + stream + " " + type + " " + id + " ");
			System.out.println(e.toString());
			return;
		}
		var reader = new BufferedReader(new InputStreamReader(inputStream));
		Double duration = null;
		Long number = null;
		Long cc = 0L;
		try {
			while(reader.ready()) {
				String line = reader.readLine();
				// #EXTINF:16.683356, stream12.ts, #EXT-X-DISCONTINUITY.
				String regex = "#EXTINF:(([0-9]*[.])?[0-9]+)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					duration = Double.parseDouble(matcher.group(1));
				} else {
					regex = "stream([0-9]+).ts";
					pattern = Pattern.compile(regex);
					matcher = pattern.matcher(line);
					if (matcher.find()) {
						number = Long.parseLong(matcher.group(1));
						if (number > maxNum) {
							StreamDataDto streamData = null;
							if (stream.equals(StreamType.live)) {
								streamData = getLiveData(type, id);
							} else {
								if (type.equals("w")) {
									streamData = webinarService.getVodData(id, cc - 1);
								} else {
									streamData = getVodData(id, cc - 1);
								}
							}
							var chunk = Chunk.builder()
								.webinarId(type.equals("w") ? id : null)
								.courseId(streamData.getId())
								.advertId(streamData.getAdvertId())
								.number(number)
								.type(stream)
								.duration(new java.sql.Time(Double.valueOf(duration * 1000).longValue() + 24 * 3600 * 1000 - zoneOffset.getTotalSeconds() * 1000))
								// .duration(
								// 	LocalTime.of(
								// 		0,
								// 		0,
								// 		Double.valueOf(duration).intValue(),
								// 		Double.valueOf((duration - Double.valueOf(duration).intValue()) * 1000 * 1000 * 1000).intValue()
								// 	)
								// )
								.active(true)
							.build();
							chunkRepository.save(chunk);
						}
					} else {
						regex = "#EXT-X-DISCONTINUITY";
						pattern = Pattern.compile(regex);
						matcher = pattern.matcher(line);
						if (matcher.find()) {
							cc++;
						}
					}
				}
			}
		} catch (java.io.IOException e) {
			System.out.println("error in manifest file. " + stream + " " + type + " " + id + " ");
			System.out.println(e.toString());
			return;
		}
	}
	@Transactional
	public void registerUsage(User user, Long webinarId, Long courseId, StreamType type, StreamUsageAction action, Long chunkNum, StreamQuality quality) {
		// TODO add quality
		Chunk chunk = null;
		if (webinarId != null) {
			chunk = chunkRepository.findOneByWebinarIdAndTypeAndNumberAndActiveIsTrue(webinarId, type, chunkNum);
		} else {
			chunk = chunkRepository.findOneByCourseIdAndTypeAndNumberAndActiveIsTrueAndWebinarIdIsNull(courseId, type, chunkNum);
		}
		if (chunk == null) {
			System.out.println("statistics error: no chunk found for " + webinarId + " " + courseId + " " + type + " " + chunkNum + " " + quality);
			return;
		}
		Long actionId = action.isDownload() ? 7L : 8L;
		var chunkUsage = ChunkUsage.builder()
			.chunk(chunk)
			.user(user)
			.point(ZonedDateTime.now())
			.actionId(actionId)
		.build();
		chunkUsageRepository.save(chunkUsage);
	}
	List<ChunkUsageResponse> getChunkUsages(GetUsageParams getUsageParams) {
		Pageable pageable = Pageable.unpaged();
		if (getUsageParams.getCount() != null) {
			pageable = PageRequest.of(0, getUsageParams.getCount(), JpaSort.unsorted()); // TODO we need indexes and sort by points or id
		}
		return chunkUsageRepository.getChunkUsages(getUsageParams, pageable);
	}

	@Transactional
	public void togglePublish(Long courseId) {
		var course  = findById(courseId);
		var published  = course.getPublished();
		course.setPublished(!published);
	}
	@Transactional
	public void publish(Long courseId) {
		var course  = findById(courseId);
		course.setPublished(true);
	}
	@Transactional
	public void unpublish(Long courseId) {
		var course  = findById(courseId);
		course.setPublished(false);
	}
}
