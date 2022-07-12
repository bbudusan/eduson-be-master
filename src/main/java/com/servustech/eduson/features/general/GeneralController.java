package com.servustech.eduson.features.general;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.auth.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
@RestController
@RequestMapping("/general")
public class GeneralController {
	
  private final JwtService jwtService;
	private final GeneralService generalService;
	
	@GetMapping("/last/{key}")
	public ResponseEntity<?> getKey(
		@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
		@PathVariable("key") String key,
		@RequestParam(required = false) Boolean o
	) {
		var user = jwtService.getUserFromAuthOk(authToken);
		try {
			return ResponseEntity.ok(generalService.getKey(key, "", user));
		} catch (NotFoundException e) {
			if (o != null && o == true) {
				return null;
			}
			throw e;
		}
	}

	@GetMapping("/last/{key}/{langCode}")
	public ResponseEntity<?> getKL(
		@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
		@PathVariable("key") String key,
		@PathVariable("langCode") String langCode,
		@RequestParam(required = false) Boolean o
	) {
		var user = jwtService.getUserFromAuthOk(authToken);
		try {
			return ResponseEntity.ok(generalService.getKey(key, langCode, user));
		} catch (NotFoundException e) {
			if (o != null && o == true) {
				return null;
			}
			throw e;
		}
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/id/{id}")
	public ResponseEntity<?> getId(
		@PathVariable("id") Long id
	) {
		return ResponseEntity.ok(generalService.getId(id));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/kv/{key}/{version}")
	public ResponseEntity<?> getKey(
		@PathVariable("key") String key,
		@PathVariable("version") Long version
	) {
		return ResponseEntity.ok(generalService.getKey(key, version, ""));
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/kvl/{key}/{version}/{langCode}")
	public ResponseEntity<?> getKey(
		@PathVariable("key") String key,
		@PathVariable("version") Long version,
		@PathVariable("langCode") String langCode
	) {
		return ResponseEntity.ok(generalService.getKey(key, version, langCode));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/id/{id}")
	public void delete(@PathVariable("id") Long id) {
		generalService.delete(id);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/all/{key}")
	public void delete(@PathVariable("key") String key) {
		generalService.delete(key);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/kv/{key}/{version}")
	public void delete(@PathVariable("key") String key,
		@PathVariable("version") Long version) {
		generalService.delete(key, version);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/kvl/{key}/{version}/{langCode}")
	public void delete(@PathVariable("key") String key,
		@PathVariable("version") Long version,
		@PathVariable("langCode") String langCode) {
		generalService.delete(key, version, langCode);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@RequestBody GeneralDto generalDto) {
		return ResponseEntity.ok(generalService.create(generalDto));
	}
	
	@GetMapping("/page")
	public ResponseEntity<?> page(
		@RequestParam(required = false) String filterByName,
		@RequestParam(required = false) String langCode,
		Pageable pageable,
		@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken
	) {
		var user = jwtService.getUserFromAuthOk(authToken);
		return ResponseEntity.ok(generalService.page(filterByName, langCode, pageable, user != null && user.isAdmin()));
	}
	
}
