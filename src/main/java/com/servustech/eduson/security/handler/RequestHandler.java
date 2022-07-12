package com.servustech.eduson.security.handler;

import com.servustech.eduson.exceptions.AppException;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.constants.ErrorCodes;
import com.servustech.eduson.security.payload.LoginRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * A handler class responsible to manipulate the request
 */
@Component
public class RequestHandler {

    /**
     * Decoding user credentials
     *
     * @return LoginRequest with user name and password
     */
    public LoginRequest decodeCredentials(String authCredentials) {
        String[] authParts = authCredentials.split("\\s+");
        String authInfo = authParts[0];
        String[] credentials = authInfo.split(":");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(credentials[0]);
        loginRequest.setPassword(credentials[1]);
        return loginRequest;
    }

    /**
     * Get jwt from request
     *
     * @return JWT
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthConstants.AUTH_KEY);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new AppException("jwt-or-bearer-missing", ErrorCodes.UNAUTHORIZED.toString());
    }

    /**
     * Get jwt from string request
     *
     * @return JWT
     */
    public String getJwtFromStringRequest(String request) {
        if (StringUtils.hasText(request) && request.startsWith("Bearer ")) {
            return request.substring(7);
        }
        throw new AppException("jwt-or-bearer-missing", ErrorCodes.UNAUTHORIZED.toString());
    }
}
