package com.servustech.eduson.features.products.webinars;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.courses.dto.AssignCoursesRequest;
import com.servustech.eduson.features.products.webinars.dto.CoursePlaceDto;
import com.servustech.eduson.utils.httpresponse.HttpResponseUtil;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.auth.JwtService;
import com.servustech.eduson.features.products.liveEvents.LiveEvent4Check;
import com.servustech.eduson.features.products.liveEvents.LiveEvent4CheckRepository;

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
@RequestMapping("/wchat2")
@RequiredArgsConstructor
public class WchatController {

	private final UserService userService;
	private final WchatService wchatService;
	private final JwtService jwtService;
	private final PermissionsService permissionsService;
	private final Webinar4CheckRepository webinar4CheckRepository;
	private final LiveEvent4CheckRepository liveEvent4CheckRepository;

	@GetMapping("/previous/{dest}/{productId}")
	public ResponseEntity<?> getPrevious(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
	@PathVariable String dest, @PathVariable String productId) {
		System.out.println("wchat get previous");
		// maybe Pageable pageable
		var user = jwtService.getUserFromAuthOk(authToken);
		if (user == null && !(dest.equals("h") && productId.length() == 20)) {
			throw new CustomException("access-denied");
		}
		if (dest.equals("w")) {
			Webinar4Check webinar = webinar4CheckRepository.findById(Long.valueOf(productId))
				.orElseThrow(() -> new NotFoundException("webinar-w-id-not-exist"));
			if (!permissionsService.hasAccessTo(user, webinar)) {
				throw new CustomException("access-denied");
			}
		} else if (dest.equals("e")) {
			LiveEvent4Check liveEvent = liveEvent4CheckRepository.findById(Long.valueOf(productId))
				.orElseThrow(() -> new NotFoundException("live-event-w-id-not-exist"));
			if (!permissionsService.hasAccessTo(user, liveEvent)) {
				throw new CustomException("access-denied");
			}
		} else if (dest.equals("h")) {
			if (productId.length() == 20) {
				return ResponseEntity.ok(wchatService.getAll(dest, productId));
			} else {
				if (!user.isAdmin() && Long.valueOf(productId) != user.getId()) {
					throw new CustomException("access-denied");
				}
			}
		} else {
			throw new NotFoundException("product-w-id-not-exist");
		}
		return ResponseEntity.ok(wchatService.getAll(dest, Long.valueOf(productId), user.isAdmin()));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/anonymous")
	public ResponseEntity<?> getAnonymous(
		@RequestParam(required = false) String filterByName, 
		Pageable pageable
	) {
		System.out.println("wchat get anonymous");
		// maybe Pageable pageable
		return ResponseEntity.ok(wchatService.getAnonymous(filterByName, pageable));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/anonymous")
	public void deleteAnonymous(
		@PathVariable String session
	) {
		System.out.println("wchat delete anonymous");
		wchatService.deleteAnonymous(session);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/logged-in")
	public ResponseEntity<?> getLoggedInUserChats(
		@RequestParam(required = false) String filterByName, 
		Pageable pageable
	) {
		System.out.println("wchat get logged-in");
		// maybe Pageable pageable
		return ResponseEntity.ok(wchatService.getLoggedInUserChats(filterByName, pageable));
	}
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/previous/hide/{id}")
	public ResponseEntity<?> deletePrevious(
	@PathVariable Long id) {
		return ResponseEntity.ok(wchatService.hide(id));
	}
}
