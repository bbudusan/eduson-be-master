package com.servustech.eduson.security.auth;

import com.servustech.eduson.security.payload.UserDetailsResponse;
import com.servustech.eduson.security.jwt.JwtTokenProvider;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import com.servustech.eduson.security.handler.RequestHandler;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.exceptions.AppException;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class JwtService {
	private final RequestHandler requestHandler;
	private final JwtTokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;

	public User getUserFromAuth(String authToken) {
		String jwt = requestHandler.getJwtFromStringRequest(authToken);
		UserDetailsResponse userDetails = tokenProvider.getUserNameAndRolesFromJWT(jwt);
		return customUserDetailsService.loadByUsername(userDetails.getUserName());
	}
	public User getUserFromAuthOk(String authToken) {
		User user = null;
		boolean weHaveToken = false;
		String jwt = null;
		if (authToken != null) {
			try {
				jwt = requestHandler.getJwtFromStringRequest(authToken);
				weHaveToken = true;
			} catch (AppException e) {
			}
		}
		if (weHaveToken) {
			try {
				UserDetailsResponse userDetails = tokenProvider.getUserNameAndRolesFromJWT(jwt);
				user = customUserDetailsService.loadByUsername(userDetails.getUserName());
			} catch (Exception e) {
			}
		}
		return user;
	}
}	
