package com.servustech.eduson.security.auth;

import com.servustech.eduson.exceptions.AlreadyExistsException;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.AccountStatus;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UserRepository;
import com.servustech.eduson.features.confirmationtoken.ConfirmationToken;
import com.servustech.eduson.features.confirmationtoken.ConfirmationTokenService;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.handler.RequestHandler;
import com.servustech.eduson.security.jwt.JwtTokenProvider;
import com.servustech.eduson.security.payload.ChangePasswordRequest;
import com.servustech.eduson.security.payload.LostPasswordRequest;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import com.servustech.eduson.utils.TokenUtils;
import com.servustech.eduson.utils.mail.MailService;
import com.servustech.eduson.utils.mail.MailSenderPostmarkService;
import com.servustech.eduson.features.permissions.StripeKeyService;
import com.servustech.eduson.features.account.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.exception.StripeException;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.HashMap;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

	private MailService mailService;
	private MailSenderPostmarkService mailSenderPostmarkService;
	private final UserRepository userRepository;
	private final ConfirmationTokenService confirmationTokenService;
	private PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;
	private final RequestHandler requestHandler;
	private final CustomUserDetailsService customUserDetailsService;
  private final StripeKeyService stripeKeyService;

	public void verifyIfUsernameOrEmailExists(String username, String email) {
		if (((username != null) && userRepository.existsByUsername(username)) ||
				(email != null) && userRepository.existsByEmail(email)) {
			throw new AlreadyExistsException("username-or-email-are-already-taken");
		}
	}

	private User createStripe(User user) {
		try {
			var metadata = new HashMap<String, String>();
			metadata.put("userId", user.getId().toString());
			CustomerCreateParams customerParams = new CustomerCreateParams.Builder()
					.putAllMetadata(metadata)
					.setEmail(user.getEmail())
					.setName(user.getFullName())
					.build();
			
			Stripe.apiKey = stripeKeyService.getApiKey();
			Customer customer = Customer.create(customerParams);
			user.setStripe(customer.getId());
		} catch (StripeException e) {
			System.out.println(e);
			throw new CustomException("could-not-create-stripe-id");
		}
		return user;
	}

	@Transactional
	public User saveUser(User user) {
		user.setAccountStatus(AccountStatus.STARTED);
		user = userRepository.save(user);
		user = createStripe(user);
		String confirmToken = TokenUtils.generateConfirmationToken();
		var token = new ConfirmationToken(confirmToken, user, "CO");
		token.setCreatedOn(ZonedDateTime.now());
		confirmationTokenService.saveToken(token);
		return user;
	}

	public void sendWelcomeEmail(User user) {
		String confirmToken = confirmationTokenService.findConfirmationTokenByEmail(user.getEmail(), "CO").getValue();
		// mailService.sendRegisterConfirmationEmail(user.getEmail(), confirmToken);
		mailSenderPostmarkService.sendRegisterConfirmationEmail(
				user.getEmail(),
				confirmToken,
				user.getFullName(),
				user.getUsername(),
				user.getPassword().equals("<none>"));
	}

	@Transactional
	public User validateTokenAndSetUserStatusToActive(String confirmationToken, String email, String password) {
		var confirmationTokenDB = confirmationTokenService.validateToken(email, confirmationToken, "CO");

		var user = confirmationTokenDB.getUser();

		if (password != null && !password.isEmpty()) {
			if (!PasswordValidator.isValid(password)) {
				throw new CustomException("password-too-easy");
			}
			user.setPassword(passwordEncoder.encode(password));
		}

		user.setAccountStatus(AccountStatus.ACTIVE);

		confirmationTokenService.deleteTokenAfterConfirmation(confirmationToken);
		return user;
	}

	@Transactional
	public void changeUserPassword(ChangePasswordRequest request, User user) {

		if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
			if (!PasswordValidator.isValid(request.getNewPassword())) {
				throw new CustomException("password-too-easy");
			}
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		} else {
			throw new CustomException("invalid-old-password");
		}
	}
	@Transactional
	public void resetUserPassword(LostPasswordRequest request, User user) {
		var confirmationTokenDB = confirmationTokenService.validateToken(request.getEmail(), request.getSecurityKey(), "PR");
		if (!PasswordValidator.isValid(request.getNewPassword())) {
			throw new CustomException("password-too-easy");
		}
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		confirmationTokenDB.setUsed(true);
	}

	public User getCurrentUser(String authToken) {

		String jwt = requestHandler.getJwtFromStringRequest(authToken);
		var userDetails = tokenProvider.getUserNameAndRolesFromJWT(jwt);
		return customUserDetailsService.loadByUsername(userDetails.getUserName());

	}

	@Transactional
	public void sendPasswordResetEmail(User user) {
		String confirmToken = TokenUtils.generateConfirmationToken();
		var token = new ConfirmationToken(confirmToken, user, "PR");
		token.setCreatedOn(ZonedDateTime.now());
		confirmationTokenService.saveToken(token);	
		String passwordResetToken = confirmationTokenService.findConfirmationTokenByEmail(user.getEmail(), "PR").getValue();
		// mailService.sendRegisterConfirmationEmail(user.getEmail(), confirmToken);
		mailSenderPostmarkService.sendPasswordResetEmail(
			user.getEmail(),
			passwordResetToken,
			user.getFullName(),
			user.getUsername());

	}
}




