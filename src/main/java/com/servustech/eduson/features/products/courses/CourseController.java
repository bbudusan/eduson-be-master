package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.products.courses.dto.CourseDto;
import com.servustech.eduson.features.products.courses.dto.AdvertDto;
import com.servustech.eduson.features.products.courses.dto.GetUsageParams;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import com.servustech.eduson.utils.httpresponse.HttpResponseUtil;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.auth.JwtService;
import com.servustech.eduson.security.payload.StreamUsageAction;
import com.servustech.eduson.security.payload.StreamQuality;
import com.servustech.eduson.security.payload.StreamType;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

	private final CourseService courseService;
	private final JwtService jwtService;
	private final HttpResponseUtil httpResponseUtil;

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/vod/{id}")
	public ResponseEntity<?> vod(@PathVariable Long id, @RequestParam(required = false) Double start, @RequestParam(required = false) Double end) {
		courseService.clearDirectory("c", id, false);
		return ResponseEntity.ok(courseService.stream(id, start == null ? 0 : start, end, false, "c", null, true, 0L));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/live/{id}")
	public ResponseEntity<?> live(@PathVariable Long id, @RequestParam(required = false) Double start, @RequestParam(required = false) Double end) {
		courseService.clearDirectory("c", id, true);
		return ResponseEntity.ok(courseService.stream(id, start == null ? 0 : start, end, true, "c", null, true, 0L));
	}

	@GetMapping("/livedata/{id}")
	public ResponseEntity<?> getLiveData(@PathVariable Long id) {
		return ResponseEntity.ok(courseService.getLiveData("c", id));
	}
	@GetMapping("/voddata/{id}/{cc}")
	public ResponseEntity<?> getVodData(@PathVariable Long id, @PathVariable Long cc) {
		return ResponseEntity.ok(courseService.getVodData(id, cc));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/stop/{id}")
	public ResponseEntity<?> stop(@PathVariable Long id) {
		return ResponseEntity.ok(courseService.stopLive("c", id) ? 
		httpResponseUtil.createHttpResponse(HttpStatus.OK, "admin.stream-stopped")
		 : httpResponseUtil.createHttpResponse(HttpStatus.OK, "admin.no-live-stream"));
	}
	@GetMapping("/newchunk/{stream}/{id}")
	public void newChunk(@PathVariable StreamType stream, @PathVariable Long id) {
		courseService.checkManifestFile(stream, "c", id, null);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_USER')")
	@PutMapping("/usage/{courseId}/{type}/{action}/{chunkNum}/{quality}")
	public void usage(
		@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
		@PathVariable Long courseId,
		@PathVariable StreamType type,
		@PathVariable StreamUsageAction action,
		@PathVariable Long chunkNum,
		@PathVariable StreamQuality quality
	) {
		var user = jwtService.getUserFromAuth(authToken);
		courseService.registerUsage(user, null, courseId, type, action, chunkNum, quality);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/chunk-usages")
	public ResponseEntity<?> getChunkUsages(
		@RequestParam(required = false) ZonedDateTime startDate,
		@RequestParam(required = false) ZonedDateTime endDate,
		@RequestParam(required = false) List<Long>userIds,
		@RequestParam(required = false) Long webinarId,
		@RequestParam(required = false) Long courseId,
		@RequestParam(required = false) StreamType streamType,
		@RequestParam(required = false) Long idAfter,
		@RequestParam(required = false) Integer count
	){
		var getUsageParams = GetUsageParams.builder()
			.startDate(startDate)
			.endDate(endDate)
			.userIds(userIds)
			.webinarId(webinarId)
			.courseId(courseId)
			.streamType(streamType)
			.idAfter(idAfter)
			.count(count)
		.build();
		return ResponseEntity.ok(courseService.getChunkUsages(getUsageParams));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<?> createCourse(@RequestPart CourseDto courseDto,
			@RequestPart(required = false) MultipartFile imageFile,
			@RequestPart(required = false) MultipartFile courseFile, User user) {
		return ResponseEntity.ok(courseService.create(courseDto, user, imageFile, courseFile));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{courseId}")
	public void deleteCourse(@PathVariable Long courseId) {
		courseService.deleteCourse(courseId);
	}

	@GetMapping("/public/page")
	public ResponseEntity<?> getAllCoursesPublic(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
	@RequestParam(required = false) String filterByName, 
	Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(courseService.getAllCoursesPublic(pageable, filterByName, user));
	}

	@GetMapping("/new")
	public ResponseEntity<?> getNewCourses(
			@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(courseService.getNewCourses(user));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/view")
	public ResponseEntity<?> viewCourses(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@RequestParam(required = false) String filterByName, Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(courseService.viewCourses(filterByName, pageable, user));
	}
	@GetMapping("/{courseId}")
	public ResponseEntity<?> findById(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long courseId) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(courseService.findById2(courseId, user));
	}

	@GetMapping("/lector/{userId}")
	public ResponseEntity<?> findCoursesByLector(
			@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken, @PathVariable Long userId,
			@RequestParam(required = false) String filterByName, Pageable pageable) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(courseService.viewCoursesByLector(userId, filterByName, user, pageable));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{courseId}")
	public ResponseEntity<?> updateCourse(@PathVariable Long courseId,
			@RequestPart CourseDto request,
			@RequestPart(required = false) MultipartFile imageFile,
			@RequestPart(required = false) MultipartFile courseFile) {
		return ResponseEntity.ok(courseService.updateCourse(courseId, request, imageFile, courseFile));

	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/webinars/{courseId}")
	public ResponseEntity<?> findWebinarsForCourse(@PathVariable Long courseId) {
		return ResponseEntity.ok(courseService.getWebinarsForCourses(courseId));
	}

	@PutMapping("/favorites/add/{courseId}")
	@Transactional
	public ResponseEntity<?> addToFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long courseId) {
		var user = jwtService.getUserFromAuth(authToken);
		var course = courseService.findById(courseId);
		course.addToFavorites(user);
		return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.OK, "Favorite added successfully"));
	}

	@PutMapping("/favorites/remove/{courseId}")
	@Transactional
	public ResponseEntity<?> removeFromFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long courseId) {
		var user = jwtService.getUserFromAuth(authToken);
		var course = courseService.findById(courseId);
		course.removeFromFavorites(user);
		return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.OK, "Favorite removed successfully"));
	}

	@GetMapping("/favorites")
	public ResponseEntity<?> getFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
	@RequestParam(required = false) String filterByName, 
	 Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(courseService.getFavoritedCourses(pageable, filterByName, user));
	}

	@GetMapping("/my")
	public ResponseEntity<?> getMyProducts(
		@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
		@RequestParam(required = false) String filterByName, 
		Pageable pageable
		) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(courseService.getMyCourses(pageable, filterByName, user));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/has-links/{id}")
	public ResponseEntity<?> hasLinks(@PathVariable Long id) {
		return ResponseEntity.ok(courseService.hasLinks(id));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/adverts-assigned/{courseId}")
	public ResponseEntity<?> getAdvertsAssigned(@PathVariable Long courseId,
			@RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(courseService.getAllAdvertsAssigned(courseId, filterByName, pageable));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/adverts-unassigned/{courseId}")
	public ResponseEntity<?> getAdvertsUnassigned(@PathVariable Long courseId,
			@RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(courseService.getAllAdvertsUnassigned(courseId, filterByName, pageable));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/assign-adverts/{courseId}")
	public void assignAdvertsToCourse(@PathVariable Long courseId, @RequestBody AssignCoursesRequest coursesRequest) {
		courseService.assignAdvertsToCourse(courseId, coursesRequest);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/unassign-adverts/{courseId}")
	public void unassignAdvertsFromCourse(@PathVariable Long courseId,
			@RequestBody AssignCoursesRequest coursesRequest) {
		courseService.unassignAdvertsFromCourse(courseId, coursesRequest);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/update-advert/{courseId}")
	public void updateAdvert(@PathVariable Long courseId,
			@RequestBody AdvertDto advertDto) {
		courseService.updateAdvert(courseId, advertDto);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{courseId}/togglepublish")
	public void togglePublish(@PathVariable Long courseId) {
		courseService.togglePublish(courseId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{courseId}/publish")
	public void publish(@PathVariable Long courseId) {
		courseService.publish(courseId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{courseId}/unpublish")
	public void unpublish(@PathVariable Long courseId) {
		courseService.unpublish(courseId);
	}
}
