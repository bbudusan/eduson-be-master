package com.servustech.eduson.security.jwt;

import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import com.servustech.eduson.security.handler.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Every http request will pass through this filter
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private RequestHandler requestHandler;
    /**
     * A filter responsible to validate the token request and set the authentication Security Context
     *
     * @param request for HttpServletRequest
     * @param response for HttpServletResponse
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = requestHandler.getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                String userName = tokenProvider.getUserNameFromJWT(jwt);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                /**
                 * We start by creating an empty SecurityContext.
                 * It is important to create a new SecurityContext instance instead of using
                 * SecurityContextHolder.getContext().setAuthentication(authentication) to avoid race conditions
                 * across multiple threads.
                 * https://docs.spring.io/spring-security/site/docs/current-SNAPSHOT/reference/html5/#servlet-authentication-securitycontextholder
                 */
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        } catch (Exception ex) {
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
