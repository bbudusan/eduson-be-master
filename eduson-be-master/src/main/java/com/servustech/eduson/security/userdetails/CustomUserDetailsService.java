package com.servustech.eduson.security.userdetails;

import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.AccountStatus;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UserRepository;
import com.servustech.eduson.utils.loginattempt.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * creating custom user details service from spring security's
 * UserDetailsService interface
 */
@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    /**
     * Get userName from database and create a user principal
     *
     * @return user principal details
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Let people login with either username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "user-by-email-or-username-not-found"));

        checkForUnconfirmedAccount(user);

        validateLoginAttempt(user);
        return new UserPrincipal(user);
    }

    @Transactional
    public User loadByUsername(String username) throws UsernameNotFoundException {
        // Let people login with either username or email
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user-by-username-not-found"));

        checkForUnconfirmedAccount(user);

        validateLoginAttempt(user);
        return user;
    }

    private void checkForUnconfirmedAccount(User user) {

        if (user.isRegisteredOnly())
            throw new CustomException("account-is-inactive");
    }

    private void validateLoginAttempt(User user) {

        if (!user.isLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setAccountStatus(AccountStatus.LOCKED);
            } else {
                user.setAccountStatus(AccountStatus.ACTIVE);
            }
        } else {
            if (!loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setAccountStatus(AccountStatus.ACTIVE);
            }
        }
    }
}
