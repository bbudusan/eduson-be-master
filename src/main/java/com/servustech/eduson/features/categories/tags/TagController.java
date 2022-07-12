package com.servustech.eduson.features.categories.tags;

import com.servustech.eduson.features.categories.tags.dto.TagDto;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
	
	private final TagService tagService;

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<?> createTag(@RequestBody TagDto tagDto) {
		return ResponseEntity.ok(tagService.create(tagDto));
	}
	
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{tagId}")
	public void deleteTag(@PathVariable Long tagId) {
		tagService.deleteTag(tagId);
	}
	
	@GetMapping("/page")
	public ResponseEntity<?> getAllTags(Pageable pageable) {
		return ResponseEntity.ok(tagService.getAllTags(pageable));
	}
	
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/{tagId}")
	public ResponseEntity<?> findById(@PathVariable Long tagId) {
		return ResponseEntity.ok(tagService.findById(tagId));
	}
	
	@GetMapping
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(tagService.getAll());
	}
	
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping
	public ResponseEntity<?> updateTag(@RequestBody TagDto request) {
		return ResponseEntity.ok(tagService.update(request));
	}
	
	@GetMapping("/category/{tagCategoryId}")
	public ResponseEntity<?> getAllTags(
		@PathVariable Long tagCategoryId,
		@RequestParam(name = "unpaged", required = false) boolean unpaged,
		@RequestParam(required = false) String filterByName,
		Pageable pageable
	) {
		return ResponseEntity.ok(tagService.getTagsByTagCategory(tagCategoryId, filterByName, unpaged ? Pageable.unpaged() : pageable));
	}
	
  // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	// TODO check if it is admin, if it is public and if hasAccess
	@GetMapping("/live-events-assigned/{tagId}")
	public ResponseEntity<?> getLiveEvents(@PathVariable Long tagId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(tagService.getLiveEvents(tagId, filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/live-events-unassigned/{tagId}")
	public ResponseEntity<?> getLiveEventsUnassigned(@PathVariable Long tagId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(tagService.getLiveEventsUnassigned(tagId, filterByName, pageable));
	}
  // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	// TODO check if it is admin, if it is public and if hasAccess
	@GetMapping("/webinars-assigned/{tagId}")
	public ResponseEntity<?> getWebinars(@PathVariable Long tagId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(tagService.getWebinars(tagId, filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/webinars-unassigned/{tagId}")
	public ResponseEntity<?> getWebinarsUnassigned(@PathVariable Long tagId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(tagService.getWebinarsUnassigned(tagId, filterByName, pageable));
	}
  // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	// TODO check if it is admin, if it is public and if hasAccess
	@GetMapping("/courses-assigned/{tagId}")
	public ResponseEntity<?> getCourses(@PathVariable Long tagId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(tagService.getCourses(tagId, filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/courses-unassigned/{tagId}")
	public ResponseEntity<?> getCoursesUnassigned(@PathVariable Long tagId, @RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(tagService.getCoursesUnassigned(tagId, filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/assign-courses/{tagId}")
	public void assignCourses(@PathVariable Long tagId, @RequestBody AssignCoursesRequest coursesRequest) {
		tagService.assignCourses(tagId, coursesRequest);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/unassign-courses/{tagId}")
	public void unassignCourses(@PathVariable Long tagId, @RequestBody AssignCoursesRequest coursesRequest) {
		tagService.unassignCourses(tagId, coursesRequest);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/assign-webinars/{tagId}")
	public void assignWebinars(@PathVariable Long tagId, @RequestBody AssignCoursesRequest webinarsRequest) {
		tagService.assignWebinars(tagId, webinarsRequest);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/unassign-webinars/{tagId}")
	public void unassignWebinars(@PathVariable Long tagId, @RequestBody AssignCoursesRequest webinarsRequest) {
		tagService.unassignWebinars(tagId, webinarsRequest);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/assign-live-events/{tagId}")
	public void assignLiveEvents(@PathVariable Long tagId, @RequestBody AssignCoursesRequest liveEventsRequest) {
		tagService.assignLiveEvents(tagId, liveEventsRequest);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/unassign-live-events/{tagId}")
	public void unassignLiveEvents(@PathVariable Long tagId, @RequestBody AssignCoursesRequest liveEventsRequest) {
		tagService.unassignLiveEvents(tagId, liveEventsRequest);
	}

}
