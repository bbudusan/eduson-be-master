package com.servustech.eduson.features.products.liveEvents;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.products.liveEvents.dto.LiveEventDto;
import com.servustech.eduson.utils.httpresponse.HttpResponseUtil;
import com.servustech.eduson.security.auth.JwtService;
import com.servustech.eduson.security.constants.AuthConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/live-event")
@RequiredArgsConstructor
public class LiveEventController {
	
	private final LiveEventService liveEventService;
	private final JwtService jwtService;
	private final HttpResponseUtil httpResponseUtil;

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<?> createLiveEvent(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
											 @RequestPart LiveEventDto liveEventDto,
											 @RequestPart(required = false) MultipartFile imageFile) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(liveEventService.create(liveEventDto, user, imageFile));
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{liveEventId}")
	public void deleteLiveEvent(@PathVariable Long liveEventId) {
		liveEventService.deleteLiveEvent(liveEventId);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_USER')")
	@GetMapping("/page")
	public ResponseEntity<?> getAllLiveEvents(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
	@RequestParam(required = false) String filterByName, 
	Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(liveEventService.getAllLiveEvents(pageable, filterByName, user));
	}

	@GetMapping("/new")
	public ResponseEntity<?> getNewLiveEvents(
			@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(liveEventService.getNewLiveEvents(user));
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/view")
	public ResponseEntity<?> viewEvents(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
	@RequestParam(required = false) String filterByName, Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(liveEventService.viewLiveEvents(filterByName, pageable, user));
	}
	
	@GetMapping("/{liveEventId}")
	public ResponseEntity<?> findById(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken, @PathVariable Long liveEventId) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(liveEventService.findById2(liveEventId, user));
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{liveEventId}")
	public ResponseEntity<?> updateLiveEvent(@PathVariable Long liveEventId,
										   @RequestPart LiveEventDto request,
										   @RequestPart(required = false) MultipartFile imageFile) {
		return ResponseEntity.ok(liveEventService.updateLiveEvent(liveEventId, request, imageFile));		
	}
	@GetMapping("/signature/{liveEventId}")
	public ResponseEntity<?> getSignature(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, @PathVariable Long liveEventId) {
		var user = jwtService.getUserFromAuth(authToken);
		// TODO check access
		return ResponseEntity.ok(liveEventService.generateSignature(liveEventId, 0));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/init/{liveEventId}")
	public ResponseEntity<?> initSession(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, @PathVariable Long liveEventId) {
		var user = jwtService.getUserFromAuth(authToken);
		// TODO check access
		return ResponseEntity.ok(liveEventService.generateSignature(liveEventId, 1));
	}
	@PutMapping("/favorites/add/{liveEventId}")
	@Transactional
	public ResponseEntity<?> addToFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long liveEventId) {
		var user = jwtService.getUserFromAuth(authToken);
		var liveEvent = liveEventService.findById(liveEventId);
		liveEvent.addToFavorites(user);
		return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.OK, "Favorite added successfully"));
	}

	@PutMapping("/favorites/remove/{liveEventId}")
	@Transactional
	public ResponseEntity<?> removeFromFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long liveEventId) {
		var user = jwtService.getUserFromAuth(authToken);
		var liveEvent = liveEventService.findById(liveEventId);
		liveEvent.removeFromFavorites(user);
		return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.OK, "Favorite removed successfully"));
	}

	@GetMapping("/favorites")
	public ResponseEntity<?> getFavorites(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
	@RequestParam(required = false) String filterByName, 
	Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(liveEventService.getFavoritedLiveEvents(pageable, filterByName, user));
	}

	@GetMapping("/my")
	public ResponseEntity<?> getMyProducts(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
	@RequestParam(required = false) String filterByName, 
	Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(liveEventService.getMyLiveEvents(pageable, filterByName, user));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/has-links/{id}")
	public ResponseEntity<?> hasLinks(@PathVariable Long id) {
		return ResponseEntity.ok(liveEventService.hasLinks(id));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{liveEventId}/togglepublish")
	public void togglePublish(@PathVariable Long liveEventId) {
		liveEventService.togglePublish(liveEventId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{liveEventId}/publish")
	public void publish(@PathVariable Long liveEventId) {
		liveEventService.publish(liveEventId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{liveEventId}/unpublish")
	public void unpublish(@PathVariable Long liveEventId) {
		liveEventService.unpublish(liveEventId);
	}
}
