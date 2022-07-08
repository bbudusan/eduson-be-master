package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.products.courses.dto.AdvertDto;
import com.servustech.eduson.utils.httpresponse.HttpResponseUtil;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.auth.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/adverts")
@RequiredArgsConstructor
public class AdvertController {

	private final AdvertService advertService;
	private final JwtService jwtService;
	private final HttpResponseUtil httpResponseUtil;

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<?> createAdvert(@RequestPart AdvertDto advertDto,
			@RequestPart(required = false) MultipartFile file,
			User user) {
		return ResponseEntity.ok(advertService.create(advertDto, user, file));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{advertId}")
	public void deleteAdvert(@PathVariable Long advertId) {
		advertService.deleteAdvert(advertId);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/view")
	public ResponseEntity<?> viewAdverts(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@RequestParam(required = false) String filterByName, Pageable pageable) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(advertService.viewAdverts(filterByName, pageable, user));
	}
	@GetMapping("/{advertId}")
	public ResponseEntity<?> findById(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
			@PathVariable Long advertId) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(advertService.findById2(advertId));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{advertId}")
	public ResponseEntity<?> updateAdvert(@PathVariable Long advertId,
			@RequestPart AdvertDto request,
			@RequestPart(required = false) MultipartFile file
			) {
		return ResponseEntity.ok(advertService.updateAdvert(advertId, request, file));

	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/courses/{advertId}")
	public ResponseEntity<?> findCoursesForAdvert(@PathVariable Long advertId) {
		return ResponseEntity.ok(advertService.getCoursesForAdverts(advertId));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/has-links/{id}")
	public ResponseEntity<?> hasLinks(@PathVariable Long id) {
		return ResponseEntity.ok(advertService.hasLinks(id));
	}

}
