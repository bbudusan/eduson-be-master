package com.servustech.eduson.config;

/*
 * Copied SessionRepositoryMessageInterceptor 
 * from package org.springframework.session.web.socket.server;
 */

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.features.products.webinars.MyJwtDecoder;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.Authentication;

public final class ModifiedSessionRepositoryMessageInterceptor
    implements ChannelInterceptor, HandshakeInterceptor {

  private static final String SPRING_SESSION_ID_ATTR_NAME = "SPRING.SESSION.ID";

  private final SessionRepository<MapSession> sessionRepository;

  private Set<SimpMessageType> matchingMessageTypes;

  private MyJwtDecoder jwtDecoder;
  private PermissionsService permissionsService;
  private CustomUserDetailsService customUserDetailsService;

  public ModifiedSessionRepositoryMessageInterceptor(SessionRepository<MapSession> sessionRepository,
      MyJwtDecoder jwtDecoder,
      PermissionsService permissionsService,
      CustomUserDetailsService customUserDetailsService) {
    Assert.notNull(sessionRepository, "sessionRepository cannot be null");
    this.sessionRepository = sessionRepository;
    this.jwtDecoder = jwtDecoder;
    this.permissionsService = permissionsService;
    this.customUserDetailsService = customUserDetailsService;
    this.matchingMessageTypes = EnumSet.of(SimpMessageType.CONNECT, SimpMessageType.MESSAGE,
        SimpMessageType.SUBSCRIBE, SimpMessageType.UNSUBSCRIBE);
  }

  public void setMatchingMessageTypes(Set<SimpMessageType> matchingMessageTypes) {
    Assert.notEmpty(matchingMessageTypes, "matchingMessageTypes cannot be null or empty");
    this.matchingMessageTypes = matchingMessageTypes;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    // if (message == null) {
    // return message;
    // }
    // SimpMessageType messageType =
    // SimpMessageHeaderAccessor.getMessageType(message.getHeaders());
    // if (!this.matchingMessageTypes.contains(messageType)) {
    // return message;
    // }
    var headers = message.getHeaders();
    Map<String, Object> sessionHeaders = SimpMessageHeaderAccessor.getSessionAttributes(headers);
    String sessionId = (sessionHeaders != null) ? (String) sessionHeaders.get(SPRING_SESSION_ID_ATTR_NAME) : null;
    if (sessionId != null) {
      SimpMessageHeaderAccessor.getSessionId(headers);
      MapSession session = this.sessionRepository.findById(sessionId);
      if (session == null) {
        MapSession newSession = new MapSession(sessionId);
        var attributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);
        attributes.entrySet().stream()
            .forEach(attribute -> newSession.setAttribute(attribute.getKey(), attribute.getValue()));
        this.sessionRepository.save(newSession);
      }
      session = this.sessionRepository.findById(sessionId);
      // update the last accessed time
      session.setLastAccessedTime(Instant.now());
      this.sessionRepository.save(session);

      StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
      if (StompCommand.CONNECT.equals(accessor.getCommand())
          || SimpMessageType.MESSAGE.equals(accessor.getMessageType())
          || SimpMessageType.SUBSCRIBE.equals(accessor.getMessageType())) {
        var destination = accessor.getDestination();
        java.util.Map<java.lang.String,java.lang.Object> claims = null;

        List<String> authorization = accessor.getNativeHeader("X-Authorization");
        if (authorization != null) {
          var authArr = authorization.get(0).split(" ");
          if (authArr.length > 1) {
            String accessToken = authArr[1];
            Jwt jwt = jwtDecoder.decode(accessToken);
            claims = jwt.getClaims();
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            Authentication authentication = converter.convert(jwt);
            accessor.setUser(authentication);
            accessor.setHeader("username", claims.get("sub"));
          }
        }
        if (SimpMessageType.SUBSCRIBE.equals(accessor.getMessageType())) {
          if (destination.startsWith("/start/initial/w/")) {
            var webinarId = Long.valueOf(destination.substring(17));
            var user = customUserDetailsService.loadByUsername(claims.get("sub").toString());
            permissionsService.checkPermissions(user, webinarId, ProductType.WEBINAR);
          }
          if (destination.startsWith("/start/initial/e/")) {
            var liveEventId = Long.valueOf(destination.substring(17));
            var user = customUserDetailsService.loadByUsername(claims.get("sub").toString());
            permissionsService.checkPermissions(user, liveEventId, ProductType.LIVE_EVENT);
          }
          if (destination.startsWith("/start/initial/h/")) {
            var sessionOrProductId = destination.substring(17);
            if (sessionOrProductId.length() < 20) {
              var user = customUserDetailsService.loadByUsername(claims.get("sub").toString());
              if (!user.isAdmin() && !Long.valueOf(sessionOrProductId).equals(user.getId())) {
                throw new CustomException("access-denied");
              }
            }
          }
        }

        // at connect and subscribe we need to check access somehow, and in case of
        // message whether the identity is valid
      }

    }

    return message;
  }

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
      Map<String, Object> attributes) {

    if (request instanceof ServletServerHttpRequest) {
      ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
      var servletReq = servletRequest.getServletRequest();
      HttpSession session = servletReq.getSession(true);
      if (session != null) {
        setSessionId(attributes, session.getId());
      }
      // URI uri = request.getURI();
      // String[] p = uri.getPath().split("/");
      // var l = p.length;
      // if (l > 1 && "websocket".equals(p[l - 1])) {
      // setSessionId(attributes, p[l - 2]);
      // }
    }
    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
      Exception exception) {
  }

  public static String getSessionId(Map<String, Object> attributes) {
    return (String) attributes.get(SPRING_SESSION_ID_ATTR_NAME);
  }

  public static void setSessionId(Map<String, Object> attributes, String sessionId) {
    attributes.put(SPRING_SESSION_ID_ATTR_NAME, sessionId);
  }

}
