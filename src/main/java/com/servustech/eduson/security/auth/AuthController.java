package com.servustech.eduson.security.auth;

import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.AccountStatus;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.account.UsersConverter;
import com.servustech.eduson.features.account.users.dto.UserDetailsResponseExt;
import com.servustech.eduson.features.account.mapper.UserMapper;
import com.servustech.eduson.features.confirmationtoken.ConfirmationTokenService;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.handler.RequestHandler;
import com.servustech.eduson.security.jwt.JwtTokenProvider;
import com.servustech.eduson.security.payload.*;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import com.servustech.eduson.utils.httpresponse.HttpResponseUtil;
import com.servustech.eduson.utils.mail.MailService;
import com.servustech.eduson.features.account.users.dto.UserDto;
import com.servustech.eduson.features.account.lectors.dto.LectorDto;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.features.account.lectors.LectorRepository;
import com.servustech.eduson.features.account.lectors.Lector;
import com.servustech.eduson.features.account.lectorTitle.LectorTitleService;
import com.servustech.eduson.features.permissions.ChangeBeneficiaryDto;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.features.products.courses.CourseService;
import com.servustech.eduson.features.products.webinars.WebinarService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Auth Controller, An entry class for all incoming requests
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	private final RequestHandler requestHandler;
	private final UserMapper userMapper;
	private final AuthService authService;
	private final HttpResponseUtil httpResponseUtil;
	private final CustomUserDetailsService customUserDetailsService;
	private final FileService fileService;
	private final LectorRepository lectorRepository;
	private final LectorTitleService lectorTitleService;
	private final UsersConverter usersConverter;
	private final UserService userService;
	private final JwtService jwtService;
	private final PermissionsService permissionsService;

	private final WebinarService webinarService;
	private final CourseService courseService;


	/**
	 * Validate the credentials and generate the jwt tokens
	 *
	 * @return access token and refresh token
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		var userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
		authenticate(loginRequest.getUsername(), loginRequest.getPassword(), userDetails.getAuthorities());
		return letIn(userDetails);
	}

	@PutMapping("/confirmation")
	public ResponseEntity<?> confirmUserAccount(@RequestBody ConfirmationRequest confirmationRequest) {
		User user = authService.validateTokenAndSetUserStatusToActive(confirmationRequest.getToken(),
				confirmationRequest.getEmail(), confirmationRequest.getPassword());
		var userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
		return letIn(userDetails);
	}

	@GetMapping("/resend")
	public ResponseEntity<?> resendMail(@Valid @RequestParam("email") String email) {
		var user = userService.findByEmail(email);
		if (!user.isRegisteredOnly()) {
			throw new CustomException("account-already-confirmed");
		}
		authService.sendWelcomeEmail(user);
		return ResponseEntity.ok(
				httpResponseUtil.createHttpResponse(HttpStatus.UNAUTHORIZED, "Emailul de confirmare a fost trimis cu succes"));
	}

	private ResponseEntity<?> letIn(UserDetails userDetails) {
		var refreshJwt = tokenProvider.generateRefreshToken(userDetails);
		var accessJwt = tokenProvider.generateAccessToken(userDetails);
		return ResponseEntity.ok(
				JwtAuthenticationResponse.builder().accessToken(accessJwt).refreshToken(refreshJwt).build());
	}

	private void authenticate(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password, authorities));
	}

	/**
	 * Validate the refresh token and generate access token
	 *
	 * @return access token
	 */
	@GetMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestHeader(AuthConstants.AUTH_KEY) String authRefreshToken) {
		System.out.println("authRefreshToken " + authRefreshToken);
		try {
			if (StringUtils.hasText(authRefreshToken)) {

				String refreshJwt = requestHandler.getJwtFromStringRequest(authRefreshToken);

				// TODO we could also do a database check here at refresh token if the token is revoked

				String userName = tokenProvider.getUserNameFromJWT(refreshJwt);

				var user = customUserDetailsService.loadUserByUsername(userName);

				String accessJwtToken = tokenProvider.generateAccessToken(user);

				boolean refreshingRefreshToken = tokenProvider.willExpireSoon(refreshJwt);
				if (refreshingRefreshToken) {
					refreshJwt = tokenProvider.generateRefreshToken(user);
					return ResponseEntity.ok(
						JwtAuthenticationResponse.builder().accessToken(accessJwtToken).refreshToken(refreshJwt).build());
				} else {
					return ResponseEntity.ok(
						JwtAuthenticationResponse.builder().accessToken(accessJwtToken).build());
				}
			} else {
				return ResponseEntity.ok(httpResponseUtil.createHttpResponse(BAD_REQUEST, "empty-token"));
			}
		} catch (Exception ex) {
			log.error("Could not set user authentication in security context" + ex.getMessage());
			return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.UNAUTHORIZED, ex.getMessage()));
		}
	}

	@PreAuthorize("hasAnyAuthority('ROLE_USER')")
	@GetMapping("/vt/{productType}/{productId}")
	public ResponseEntity<?> videoToken(
		@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
		@PathVariable ProductType productType,
		@PathVariable Long productId,
		@RequestParam Long num,
		@RequestParam StreamType type
	) {
		var user = jwtService.getUserFromAuth(authToken);
		permissionsService.checkPermissions(user, productId, productType);
		String videoToken = tokenProvider.generateVideoToken(user, productId, productType, num, type);
		if (productType.isWebinar()) {
			webinarService.registerUsage(user, productId, type, StreamUsageAction.download, num, StreamQuality.high); // quality
		} else if (productType.isCourse()) {
			courseService.registerUsage(user, null, productId, type, StreamUsageAction.download, num, StreamQuality.high); // quality
		}
		return ResponseEntity.ok(
						JwtAuthenticationResponse.builder().vt(videoToken).build());
	}

	/**
	 * Validate the access token and returns user details
	 *
	 * @return status of token
	 */
	@GetMapping("/user-details")
	public ResponseEntity<?> details(@RequestHeader(AuthConstants.AUTH_KEY) String authToken) {
		var user = jwtService.getUserFromAuth(authToken);
		var response = usersConverter.fromUserToUserDetailsResponse(user);
		return ResponseEntity.ok(response);
	}

	/**
	 * This is for user registration
	 *
	 * @return user registration status
	 */
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> registerUser // (@Valid @RequestBody RegisterRequest registerRequest)
	(@RequestPart UserDto userDto,
			@RequestPart(required = false) MultipartFile profilePicture) {

		authService.verifyIfUsernameOrEmailExists(userDto.getUsername(), userDto.getEmail());

		User user = userMapper.signUpRequestToUser(userDto);
		if (profilePicture != null) {
			var image = fileService.saveWithFile(profilePicture);
			user.setProfileImage(image);
		}

		user.setAccountStatus(AccountStatus.STARTED);

		user = authService.saveUser(user);
		if (userDto.isSendWelcomeEmail()) {
			authService.sendWelcomeEmail(user);
		}

		return ResponseEntity.ok(
				httpResponseUtil.createHttpResponse(HttpStatus.CREATED, "User registered successfully"));
	}

	// TODO must be admin if without password! // (split into two commands and send
	// mail always if the user registered)
	@PostMapping("/register/lector")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> registerLector(@RequestPart LectorDto userDto,
			@RequestPart(required = false) MultipartFile profilePicture) {
		authService.verifyIfUsernameOrEmailExists(userDto.getUsername(), userDto.getEmail());

		User user = userMapper.signUpRequestToLector(userDto);
		if (profilePicture != null) {
			var image = fileService.saveWithFile(profilePicture);
			user.setProfileImage(image);
		}

		user.setAccountStatus(AccountStatus.STARTED);

		user = authService.saveUser(user);

		var title = lectorTitleService.findById(userDto.getTitleId());
		var lector = Lector.builder()
				.user(user)
				.description(userDto.getDescription())
				.hasAccess(userDto.isHasAccess())
				.title(title)
				.build();
		lectorRepository.save(lector);

		if (userDto.isSendWelcomeEmail()) {
			System.out.println("isSendWelcomeEmail");
			authService.sendWelcomeEmail(user);
		}

		return ResponseEntity.ok(
				httpResponseUtil.createHttpResponse(HttpStatus.CREATED, "Lector user registered successfully"));
	}

	@PostMapping("/register/admin")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> registerAdmin(@RequestPart UserDto userDto,
			@RequestPart(required = false) MultipartFile profilePicture) {
		authService.verifyIfUsernameOrEmailExists(userDto.getUsername(), userDto.getEmail());

		User user = userMapper.signUpRequestToAdmin(userDto);
		if (profilePicture != null) {
			var image = fileService.saveWithFile(profilePicture);
			user.setProfileImage(image);
		}

		user.setAccountStatus(AccountStatus.STARTED);

		user = authService.saveUser(user);
		if (userDto.isSendWelcomeEmail()) {
			authService.sendWelcomeEmail(user);
		}

		return ResponseEntity.ok(
				httpResponseUtil.createHttpResponse(HttpStatus.CREATED, "Admin user registered successfully"));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_LECTOR')")
	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
			@Valid @RequestBody ChangePasswordRequest request) {
		try {
			var user = jwtService.getUserFromAuth(authToken);
			authService.changeUserPassword(request, user);

			return ResponseEntity.ok(
					httpResponseUtil.createHttpResponse(HttpStatus.OK, "User password changed successfully"));
		} catch (Exception ex) {
			log.error("Could not set user authentication in security context" + ex.getMessage());
			return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.UNAUTHORIZED, ex.getMessage()));
		}
	}
	// TODO i think it is not okay to send security keys in the url. Alice ----x---> Bob. also problem at activating the account. what to do?
	@PostMapping("/lost-password")
	public ResponseEntity<?> lostPassword(
			@Valid @RequestBody LostPasswordRequest request) {
		try {
			var user = userService.findByEmail(request.getEmail());
			authService.resetUserPassword(request, user);
			return ResponseEntity.ok(
				ChangeBeneficiaryDto.builder().response("success").build());
		} catch (Exception ex) {
			// TODO we should not tell whether the email is found
			log.error("Could not get user in security context" + ex.getMessage());
			return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.UNAUTHORIZED, ex.getMessage()));
		}
	}
	@PostMapping("/lost-password-email")
	public ResponseEntity<?> lostPasswordEmail(
		@Valid @RequestBody LostPasswordEmailRequest request) {
		try {
			var user = userService.findByEmail(request.getEmail());
			authService.sendPasswordResetEmail(user);
			return ResponseEntity.ok(
				ChangeBeneficiaryDto.builder().response("success").build());
		} catch (Exception ex) {
			// we should not tell publicly whether the email is registered or not. (and we should not tell this by timing either... TODO )
			// and we should apply captchas. TODO also for lost password, for register and login.
			return ResponseEntity.ok(
				ChangeBeneficiaryDto.builder().response("success").build());

			// log.error("Could not get user in security context" + ex.getMessage());
			// return ResponseEntity.ok(httpResponseUtil.createHttpResponse(HttpStatus.UNAUTHORIZED, ex.getMessage()));
		}
	}
}
