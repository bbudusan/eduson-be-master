package com.servustech.eduson.features.products.webinars;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import com.servustech.eduson.features.products.webinars.dto.WebinarDto;
import com.servustech.eduson.features.products.webinars.dto.CoursePlaceDto;
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

import java.util.List;

@RestController
@RequestMapping("/webinar")
@RequiredArgsConstructor
public class WebinarController {

	private final WebinarService webinarService;
	private final JwtService jwtService;
	private final HttpResponseUtil httpResponseUtil;

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/vod/{id}")
	public ResponseEntity<?> vod(@PathVariable Long id) {
		return ResponseEntity.ok(webinarService.stream(id, false));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/live/{id}")
	public ResponseEntity<?> live(@PathVariable Long id) {
		return ResponseEntity.ok(webinarService.stream(id, true));
	}
	@PutMapping("/livehdsfbjdsfsbfsahfsavf/{id}")
	public ResponseEntity<?> livehdsfbjdsfsbfsahfsavf(@PathVariable Long id) {
		return ResponseEntity.ok(webinarService.stream(id, true));
	}
	@GetMapping("/livedata/{id}")
	public ResponseEntity<?> getLiveData(@PathVariable Long id) {
		return ResponseEntity.ok(webinarService.getLiveData(id));
	}
	@GetMapping("/voddata/{id}/{cc}")
	public ResponseEntity<?> getVodData(@PathVariable Long id, @PathVariable Long cc) {
		return ResponseEntity.ok(webinarService.getVodData(id, cc));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/stop/{id}")
	public ResponseEntity<?> stop(@PathVariable Long id) {
		return ResponseEntity.ok(webinarService.stopLive(id) ?
		httpResponseUtil.createHttpResponse(HttpStatus.OK, "admin.stream-stopped")
		 : httpResponseUtil.createHttpResponse(HttpStatus.OK, "admin.no-live-stream"));
	}
	@GetMapping("/newchunk/{stream}/{id}")
	public void newChunk(@PathVariable StreamType stream, @PathVariable Long id) {
		webinarService.checkManifestFile(stream, id);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_USER')")
	@PutMapping("/usage/{webinarId}/{type}/{action}/{chunkNum}/{quality}")
	public void usage(
		@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
		@PathVariable Long webinarId,
		@PathVariable StreamType type,
		@PathVariable StreamUsageAction action,
		@PathVariable Long chunkNum,
		@PathVariable StreamQuality quality
	) {
		var user = jwtService.getUserFromAuth(authToken);
		webinarService.registerUsage(user, webinarId, type, action, chunkNum, quality);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<?> createWebinar(@RequestPart WebinarDto webinarDto,
			@RequestPart(required = false) MultipartFile imageFile,
			User user) {
		return ResponseEntity.ok(webinarService.create(webinarDto, user, imageFile));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{webinarId}")
	public ResponseEntity<?> updateWebinar(@PathVariable Long webinarId,
			@RequestPart WebinarDto request,
			@RequestPart(required = false) MultipartFile imageFile) {
		return ResponseEntity.ok(webinarService.updateWebinar(webinarId, request, imageFile));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{webinarId}")
	public void deleteWebinar(@PathVariable Long webinarId) {
		webinarService.deleteWebinar(webinarId);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_USER')")
	@GetMapping("/page")
	public ResponseEntity<?> getAllWebinars(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
	@RequestParam(required = false) String filterByName, 
	Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(webinarService.getAllWebinars(pageable, filterByName, user));
	}
	@GetMapping("/new")
	public ResponseEntity<?> getNewWebinars(
			@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(webinarService.getNewWebinars(user));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/view")
	public ResponseEntity<?> viewWebinars(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@RequestParam(required = false) String filterByName, Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(webinarService.viewWebinars(filterByName, pageable, user));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/assign-courses/{webinarId}")
	public void assignCoursesToWebinar(@PathVariable Long webinarId, @RequestBody AssignCoursesRequest coursesRequest) {
		webinarService.assignCoursesToWebinar(webinarId, coursesRequest);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/unassign-courses/{webinarId}")
	public void unassignCoursesFromWebinar(@PathVariable Long webinarId,
			@RequestBody AssignCoursesRequest coursesRequest) {
		webinarService.unassignCoursesFromWebinar(webinarId, coursesRequest);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/courses-assigned/{webinarId}")
	public ResponseEntity<?> getCoursesAssigned(@PathVariable Long webinarId,
			@RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(webinarService.getAllCoursesAssigned(webinarId, filterByName, pageable));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/courses-unassigned/{webinarId}")
	public ResponseEntity<?> getCoursesUnassigned(@PathVariable Long webinarId,
			@RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(webinarService.getAllCoursesUnassigned(webinarId, filterByName, pageable));
	}
	@GetMapping("/courses-ext/{webinarId}")
	public ResponseEntity<?> getCoursesForWebinarExt(
			@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken, @PathVariable Long webinarId) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(webinarService.getAllCoursesExt(webinarId, user));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/saveorder/{webinarId}")
	public void saveCoursesPlace(@PathVariable Long webinarId, @RequestBody CoursePlaceDto coursesPlace) {
		webinarService.saveCoursesPlace(webinarId, coursesPlace);
	}

	@GetMapping("/{webinarId}")
	public ResponseEntity<?> findById(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long webinarId) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(webinarService.findById2(webinarId, user));
	}

	@PutMapping("/favorites/add/{webinarId}")
	@Transactional
	public ResponseEntity<?> addToFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long webinarId) {
		var user = jwtService.getUserFromAuth(authToken);
		var webinar = webinarService.findById(webinarId);
		webinar.addToFavorites(user);
		return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.OK, "Favorite added successfully"));
	}
	@PutMapping("/favorites/remove/{webinarId}")
	@Transactional
	public ResponseEntity<?> removeFromFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long webinarId) {
		var user = jwtService.getUserFromAuth(authToken);
		var webinar = webinarService.findById(webinarId);
		webinar.removeFromFavorites(user);
		return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.OK, "Favorite removed successfully"));
	}
	@GetMapping("/favorites")
	public ResponseEntity<?> getFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
	@RequestParam(required = false) String filterByName, 
	Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(webinarService.getFavoritedWebinars(pageable, filterByName, user));
	}

	@GetMapping("/my")
	public ResponseEntity<?> getMyProducts(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
	@RequestParam(required = false) String filterByName, 
	Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(webinarService.getMyWebinars(pageable, filterByName, user));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/has-links/{id}")
	public ResponseEntity<?> hasLinks(@PathVariable Long id) {
		return ResponseEntity.ok(webinarService.hasLinks(id));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping("/get-courses-tags") // TODO used post, but should have been a get
	public ResponseEntity<?> getCoursesTags(@RequestBody AssignCoursesRequest coursesRequest) {
		return ResponseEntity.ok(webinarService.getCoursesTags(coursesRequest));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{webinarId}/togglepublish")
	public void togglePublish(@PathVariable Long webinarId) {
		webinarService.togglePublish(webinarId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{webinarId}/publish")
	public void publish(@PathVariable Long webinarId) {
		webinarService.publish(webinarId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{webinarId}/unpublish")
	public void unpublish(@PathVariable Long webinarId) {
		webinarService.unpublish(webinarId);
	}
}
