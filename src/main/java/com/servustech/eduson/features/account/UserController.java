package com.servustech.eduson.features.account;

import com.servustech.eduson.features.account.role.RoleName;
import com.servustech.eduson.features.account.users.data.EmcService;
import com.servustech.eduson.features.account.users.data.IndividualService;
import com.servustech.eduson.features.account.users.data.LegalService;
import com.servustech.eduson.features.account.users.dto.UserPersonalDataDto;
import com.servustech.eduson.features.account.users.dto.EmcDto;
import com.servustech.eduson.features.account.users.dto.IndividualDto;
import com.servustech.eduson.features.account.users.dto.LegalDto;
import com.servustech.eduson.features.account.UserRepository;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.payload.UserDetailsResponse;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.security.auth.AuthService;
import com.servustech.eduson.features.account.users.dto.UserDto;
import com.servustech.eduson.security.auth.JwtService;
import com.servustech.eduson.features.permissions.ChangeBeneficiaryDto; // TODO generalize this
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final UserRepository userRepository;
	private final FileService fileService;
	private final EmcService emcService;
	private final IndividualService individualService;
	private final LegalService legalService;
	private final AuthService authService;
  private final JwtService jwtService;

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public String getCurrentUser(User user) {

		return "Congratulation User you can access this api";
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/{userId}")
	ResponseEntity<?> updateUser(@RequestPart UserDto userDto,
			@RequestPart(required = false) MultipartFile profilePicture) {
		return ResponseEntity.ok(userService.updateUser(userDto, profilePicture));
	}


	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.findById2(userId));
	}

	@PutMapping("/personal-data")
	public ResponseEntity<?> setPersonalData(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@RequestBody UserPersonalDataDto request) {
			var user = jwtService.getUserFromAuth(authToken);
			if (request.getEmail() != null && !user.getEmail().equals(request.getEmail())) {
			authService.verifyIfUsernameOrEmailExists(null, request.getEmail()); // TODO can we change the username?
			// TODO in this case we should send a verify message to the new email, and if
			// verified, change it
		}
		if (request.getFirstName() != null) {
			user.setFirstName(request.getFirstName());
		}
		if (request.getLastName() != null) {
			user.setLastName(request.getLastName());
		}
		if (request.getEmail() != null) {
			user.setEmail(request.getEmail());
		}
		userRepository.save(user);

		return ResponseEntity.ok(user);
	}

	@PutMapping("/profile-picture")
	public ResponseEntity<?> setProfilePicture // (@Valid @RequestBody RegisterRequest registerRequest)
	(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, @RequestPart MultipartFile profilePicture) {
		var user = jwtService.getUserFromAuth(authToken);
		var image = fileService.saveWithFile(profilePicture);
		user.setProfileImage(image);
		userRepository.save(user);
		return ResponseEntity.ok(user);
	}

	@PutMapping("/emc")
	public ResponseEntity<?> setEmc(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@RequestBody EmcDto request) {
		var user = jwtService.getUserFromAuth(authToken);
		var emc = emcService.findById(user.getId()).orElseGet(() -> emcService.createWithId(user.getId()));
		emc.setEmc(request.isEmc());
		emc.setGrade(request.getGrade());
		emc.setCuim(request.getCuim());
		emc.setSpecialty(request.getSpecialty());
		emc.setJob(request.getJob());
		emcService.save(emc);

		return ResponseEntity.ok(emc);
	}

	@GetMapping("/emc")
	public ResponseEntity<?> getEmc(@RequestHeader(AuthConstants.AUTH_KEY) String authToken) {
		var user = jwtService.getUserFromAuth(authToken);
		var emc = emcService.findById(user.getId()).orElse(null);

		return ResponseEntity.ok(emc);
	}

	@PutMapping("/individual/{userId}")
	public ResponseEntity<?> setIndividual(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@RequestBody IndividualDto request, @PathVariable Long userId) {
		var user = jwtService.getUserFromAuth(authToken);
		if (user.isAdmin() && userId > 0) {
			user = userService.findById(userId);
		}
		var id = user.getId();
		var individual = individualService.findById(id)
				.orElseGet(() -> individualService.createWithId(id));
		individual.setCnp(request.getCnp());
		individual.setAddress(request.getAddress());
		individual.setCountry(request.getCountry());
		individual.setCounty(request.getCounty());
		individual.setCity(request.getCity());
		individual.setZipCode(request.getZipCode());
		individual.setPhone(request.getPhone());
		individualService.save(individual);
		user.setInvoiceAddressPersonal(true);
		userRepository.save(user);

		return ResponseEntity.ok(individual);
	}

	@GetMapping("/individual/{userId}")
	public ResponseEntity<?> getIndividual(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, @PathVariable Long userId) {
		var user = jwtService.getUserFromAuth(authToken);
		if (user.isAdmin() && userId > 0) {
			user = userService.findById(userId);
		}
		var individual = individualService.findById(user.getId()).orElse(null);

		return ResponseEntity.ok(individual);
	}

	@PutMapping("/legal/{userId}")
	public ResponseEntity<?> setLegal(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@RequestBody LegalDto request, @PathVariable Long userId) {
		var user = jwtService.getUserFromAuth(authToken);
		if (user.isAdmin() && userId > 0) {
			user = userService.findById(userId);
		}
		var id = user.getId();
		var legal = legalService.findById(id).orElseGet(() -> legalService.createWithId(id));
		legal.setCompany(request.getCompany());
		legal.setCui(request.getCui());
		legal.setRegCom(request.getRegCom());
		legal.setIban(request.getIban());
		legal.setAddress(request.getAddress());
		legal.setCountry(request.getCountry());
		legal.setCounty(request.getCounty());
		legal.setCity(request.getCity());
		legal.setZipCode(request.getZipCode());
		legal.setPhone(request.getPhone());
		legalService.save(legal);
		user.setInvoiceAddressPersonal(false);
		userRepository.save(user);

		return ResponseEntity.ok(legal);
	}

	@GetMapping("/legal/{userId}")
	public ResponseEntity<?> getLegal(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, @PathVariable Long userId) {
		var user = jwtService.getUserFromAuth(authToken);
		if (user.isAdmin() && userId > 0) {
			user = userService.findById(userId);
		}
		var legal = legalService.findById(user.getId()).orElse(null);

		return ResponseEntity.ok(legal);
	}

	@GetMapping("/invoice-address-type/{userId}")
	public ResponseEntity<?> getInvoiceAddressType(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, @PathVariable Long userId) {
		var user = jwtService.getUserFromAuth(authToken);
		if (user.isAdmin() && userId > 0) {
			user = userService.findById(userId);
		}
		var ret = user.getInvoiceAddressPersonal();

		return ResponseEntity.ok(ret);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/lectors")
	public ResponseEntity<?> getLectors() {
		return ResponseEntity.ok(userService.getAllByRole(RoleName.ROLE_LECTOR));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/users")
	public ResponseEntity<?> getUsers() {
		return ResponseEntity.ok(userService.getAllByRole(RoleName.ROLE_USER));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/admins")
	public ResponseEntity<?> getAdmins() {
		return ResponseEntity.ok(userService.getAllByRole(RoleName.ROLE_ADMIN));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/is-lector-linked-to-any-product/{lectorId}")
	public ResponseEntity<?> isLectorLinkedToAnyProduct(@PathVariable Long lectorId) {
		return ResponseEntity.ok(userService.isLectorLinkedToAnyProduct(lectorId));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/is-linked-to-any-product/{userId}")
	public ResponseEntity<?> isLinkedToAnyProduct(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.isLinkedToAnyProduct(userId));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/admins/view")
	public ResponseEntity<?> viewAdmins(@RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(userService.viewUsers(filterByName, pageable, RoleName.ROLE_ADMIN));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/users/view")
	public ResponseEntity<?> viewUsers(@RequestParam(required = false) String filterByName, Pageable pageable) {
		return ResponseEntity.ok(userService.viewUsers(filterByName, pageable, RoleName.ROLE_USER));
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/togglerole/{userId}/{role}")
	public void toggleRole(
		@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
		@PathVariable Long userId,
		@PathVariable RoleName role
	) {
		var admin = jwtService.getUserFromAuth(authToken);
		userService.toggleRole(userId, role, admin);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/users/short")
	public ResponseEntity<?> viewUsersShort(@RequestParam(required = false) String filterByName) {
		return ResponseEntity.ok(userService.viewUsersShort(filterByName, RoleName.ROLE_USER));
	}
	@GetMapping("/counties/ro")
	public ResponseEntity<?> getCountiesRo() {
		return ResponseEntity.ok(ChangeBeneficiaryDto.builder().response(userService.getCountiesRo()).build());
	}
	@GetMapping("/cities/ro/{county}")
	public ResponseEntity<?> getCitiesRo(@PathVariable String county) {
		return ResponseEntity.ok(ChangeBeneficiaryDto.builder().response(userService.getCitiesRo(county)).build());
	}
	@GetMapping("/countries")
	public ResponseEntity<?> getCountries() {
		return ResponseEntity.ok(ChangeBeneficiaryDto.builder().response(userService.getCountries()).build());
	}

}
