package com.servustech.eduson.features.invitation;

import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.auth.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
@RestController
@RequestMapping("/invitation")
public class InvitationController {
	
  private final JwtService jwtService;
	private final InvitationService invitationService;
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_LECTOR')")
	@GetMapping("/by-me")
	public ResponseEntity<?> getInvitedByMe(
		@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
		Pageable pageable
	) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(invitationService.getInvitedBy(user, pageable));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_LECTOR')")
	@GetMapping("/my-sponsors")
	public ResponseEntity<?> getMySponsors(
		@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
		Pageable pageable
	) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(invitationService.getSponsorsOf(user, pageable));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_LECTOR')")
	@PostMapping("/invite")
	public ResponseEntity<?> invite(
		@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken, 
    @RequestBody InvitationDto invitationDto
	) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(invitationService.invite(user, invitationDto));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_LECTOR')")
	@PutMapping("/accept")
	public ResponseEntity<?> accept(
		@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken, 
    @RequestBody InvitationDto invitationDto
	) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(invitationService.accept(user, invitationDto));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_LECTOR')")
	@PutMapping("/reject")
	public ResponseEntity<?> reject(
		@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken, 
    @RequestBody InvitationDto invitationDto
	) {
		var user = jwtService.getUserFromAuth(authToken);
		return ResponseEntity.ok(invitationService.reject(user, invitationDto));
	}
}
