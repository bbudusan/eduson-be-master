package com.servustech.eduson.features.account.lectors;

import com.servustech.eduson.features.account.lectors.dto.LectorDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/lector")
@AllArgsConstructor
public class LectorController {
	
	private final LectorService lectorService;
	
	// @PostMapping
	// public ResponseEntity<?> createLector(@RequestPart LectorDto lectorDto,
	// 									  @RequestPart MultipartFile profilePicture) {
	// 	return ResponseEntity.ok(lectorService.saveLector(lectorDto, profilePicture));
	// }

	// TODO must be admin Imortant!!
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{lectorId}") ResponseEntity<?> updateLector(@RequestPart LectorDto userDto,
	 									  @RequestPart(required = false) MultipartFile profilePicture) {
	 	return ResponseEntity.ok(lectorService.updateLector(userDto, profilePicture));
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/view")
	public ResponseEntity<?> viewLectors(@RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(lectorService.viewLectors(filterByName, pageable));
	}
	@GetMapping("/viewpublic")
	public ResponseEntity<?> viewLectorsPublic(@RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(lectorService.viewLectorsPublic(filterByName, pageable));
	}
	
	@GetMapping("/profile/{userId}") // TODO maybe lector id?
	public ResponseEntity<?> getLectorProfile(@PathVariable Long userId) {
		return ResponseEntity.ok(lectorService.getLectorProfile(userId));
	}
	
	@GetMapping("/public")
	public ResponseEntity<?> getLectors() {
		return ResponseEntity.ok(lectorService.getLectorsWider());
	}
	@GetMapping("/narrow")
	public ResponseEntity<?> getLectorsNarrow() {
		return ResponseEntity.ok(lectorService.getLectorsNarrow());
	}
	
	@GetMapping("/{lectorId}")
	public ResponseEntity<?> getLectorById(@PathVariable Long lectorId) {
		return ResponseEntity.ok(lectorService.getLectorById(lectorId));
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{lectorId}")
	public void deleteLector(@PathVariable Long lectorId) {
		lectorService.deleteLector(lectorId);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/give-access/{lectorId}")
	public void giveAccess(@PathVariable Long lectorId) {
		lectorService.giveAccess(lectorId);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/remove-access/{lectorId}")
	public void removeAccess(@PathVariable Long lectorId) {
		lectorService.removeAccess(lectorId);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{lectorId}/togglepublish")
	public void togglePublish(@PathVariable Long lectorId) {
		lectorService.togglePublish(lectorId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{lectorId}/publish")
	public void publish(@PathVariable Long lectorId) {
		lectorService.publish(lectorId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{lectorId}/unpublish")
	public void unpublish(@PathVariable Long lectorId) {
		lectorService.unpublish(lectorId);
	}
}
