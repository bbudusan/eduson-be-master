package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.auth.JwtService;
import com.servustech.eduson.features.categories.modules.dto.ModuleDto;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
public class ModuleController {

	private final JwtService jwtService;	
	private final ModuleService moduleService;
	
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<?> createModule(@RequestBody ModuleDto request) {
		return ResponseEntity.ok(moduleService.create(request));
	}
	
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{moduleId}")
	public void deleteModule(@PathVariable Long moduleId) {
		moduleService.deleteModule(moduleId);
	}
	
	@GetMapping("/page")
	public ResponseEntity<?> getAllModules(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken, @RequestParam(required = false) String filterByName, Pageable pageable) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(moduleService.getAllModules(filterByName, pageable, user));
	}
	
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{moduleId}")
	public ResponseEntity<?> updateModule(@PathVariable Long moduleId, @RequestBody ModuleDto request) {
		return ResponseEntity.ok(moduleService.update(moduleId, request));
	}
	
	@GetMapping("/live-events-assigned/{moduleId}")
	public ResponseEntity<?> getLiveEvents(@PathVariable Long moduleId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(moduleService.getLiveEvents(moduleId, filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/live-events-unassigned/{moduleId}")
	public ResponseEntity<?> getLiveEventUnassigned(@PathVariable Long moduleId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(moduleService.getLiveEventsUnassigned(moduleId, filterByName, pageable));
	}
	// TODO if not admin, filter unpublished for all
	@GetMapping("/webinars-assigned/{moduleId}")
	public ResponseEntity<?> getWebinars(@PathVariable Long moduleId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(moduleService.getWebinars(moduleId, filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/webinars-unassigned/{moduleId}")
	public ResponseEntity<?> getWebinarsUnassigned(@PathVariable Long moduleId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(moduleService.getWebinarsUnassigned(moduleId, filterByName, pageable));
	}
	@GetMapping("/courses-assigned/{moduleId}")
	public ResponseEntity<?> getCourses(@PathVariable Long moduleId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(moduleService.getCourses(moduleId, filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/courses-unassigned/{moduleId}")
	public ResponseEntity<?> getCoursesUnassigned(@PathVariable Long moduleId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(moduleService.getCoursesUnassigned(moduleId, filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/assign-courses/{moduleId}")
	public void assignCourses(@PathVariable Long moduleId, @RequestBody AssignCoursesRequest coursesRequest) {
		moduleService.assignCourses(moduleId, coursesRequest);
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/unassign-courses/{moduleId}")
	public void unassignCourses(@PathVariable Long moduleId, @RequestBody AssignCoursesRequest coursesRequest) {
		moduleService.unassignCourses(moduleId, coursesRequest);
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/assign-webinars/{moduleId}")
	public void assignWebinars(@PathVariable Long moduleId, @RequestBody AssignCoursesRequest webinarsRequest) {
		moduleService.assignWebinars(moduleId, webinarsRequest);
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/unassign-webinars/{moduleId}")
	public void unassignWebinars(@PathVariable Long moduleId, @RequestBody AssignCoursesRequest webinarsRequest) {
		moduleService.unassignWebinars(moduleId, webinarsRequest);
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/assign-live-events/{moduleId}")
	public void assignLiveEvents(@PathVariable Long moduleId, @RequestBody AssignCoursesRequest liveEventsRequest) {
		moduleService.assignLiveEvents(moduleId, liveEventsRequest);
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/unassign-live-events/{moduleId}")
	public void unassignLiveEvents(@PathVariable Long moduleId, @RequestBody AssignCoursesRequest liveEventsRequest) {
		moduleService.unassignLiveEvents(moduleId, liveEventsRequest);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/has-links/{id}")
	public ResponseEntity<?> hasLinks(@PathVariable Long id) {
		return ResponseEntity.ok(moduleService.hasLinks(id));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{moduleId}/togglepublish")
	public void togglePublish(@PathVariable Long moduleId) {
		moduleService.togglePublish(moduleId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{moduleId}/publish")
	public void publish(@PathVariable Long moduleId) {
		moduleService.publish(moduleId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{moduleId}/unpublish")
	public void unpublish(@PathVariable Long moduleId) {
		moduleService.unpublish(moduleId);
	}
}
