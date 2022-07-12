package com.servustech.eduson.features.account.mapper;

import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.AccountStatus;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.role.RoleService;
import com.servustech.eduson.security.payload.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.servustech.eduson.features.account.users.dto.UserDto;
import com.servustech.eduson.features.account.lectors.dto.LectorDto;
import com.servustech.eduson.features.account.PasswordValidator;

import java.util.Collections;
import java.util.Set;

public abstract class UserMapperDecorator implements UserMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;

    private User theFormerDoesntWork1(UserDto r) { // TODO
        return User.builder()
                .firstName(r.getFirstName())
                .lastName(r.getLastName())
                .email(r.getEmail())
                .username(r.getUsername()).build();

    }

    private User theFormerDoesntWork2(LectorDto r) { // TODO
        return User.builder()
                .firstName(r.getFirstName())
                .lastName(r.getLastName())
                .email(r.getEmail())
                .username(r.getUsername()).build();

    }

    @Override
    public User signUpRequestToUser(UserDto registerRequest) {
        User user = userMapper.signUpRequestToUser(registerRequest);
        user = theFormerDoesntWork1(registerRequest); // TODO eladand
        String password = registerRequest.getPassword();
        if (password != null && password.length() > 0 && !password.equals("<none>")) {
			if (!PasswordValidator.isValid(password)) {
				throw new CustomException("password-too-easy");
			}
        }
        if (password != null && password.length() > 0) {
            user.setPassword(password.equals("<none>") ? password : passwordEncoder.encode(password));
        }
        user.setAccountStatus(AccountStatus.INACTIVE);
        user.setRoles(Collections.singleton(roleService.getUserRole()));

        return user;
    }

    @Override
    public User signUpRequestToLector(LectorDto registerRequest) {
        User user = userMapper.signUpRequestToLector(registerRequest);
        user = theFormerDoesntWork2(registerRequest);
        String password = registerRequest.getPassword();
        if (password != null && password.length() > 0 && !password.equals("<none>")) {
			if (!PasswordValidator.isValid(password)) {
				throw new CustomException("password-too-easy");
			}
        }
        if (password != null && password.length() > 0) {
            user.setPassword(password.equals("<none>") ? password : passwordEncoder.encode(password));
        }

        user.setAccountStatus(AccountStatus.INACTIVE);
        user.setRoles(Set.of(roleService.getUserRole(), roleService.getLectorRole()));

        return user;
    }

    @Override
    public User signUpRequestToAdmin(UserDto registerRequest) {
        User user = userMapper.signUpRequestToAdmin(registerRequest);
        user = theFormerDoesntWork1(registerRequest);
        String password = registerRequest.getPassword();
        if (password != null && password.length() > 0 && !password.equals("<none>")) {
			if (!PasswordValidator.isValid(password)) {
				throw new CustomException("password-too-easy");
			}
        }
        if (password != null && password.length() > 0) {
            user.setPassword(password.equals("<none>") ? password : passwordEncoder.encode(password));
        }
        user.setAccountStatus(AccountStatus.INACTIVE);
        user.setRoles(Set.of(roleService.getUserRole(), roleService.getAdminRole()));

        return user;

    }
}
