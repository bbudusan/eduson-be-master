package com.servustech.eduson.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig
    extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    messages
        .simpTypeMatchers(SimpMessageType.CONNECT).permitAll()//.authenticated()
        .nullDestMatcher().denyAll()
        // .simpSubscribeDestMatchers("/start/initial/**/*").hasRole("USER")
        // .simpDestMatchers("/current/resume/*").hasRole("USER")
        // .simpTypeMatchers(SimpMessageType.MESSAGE,
        // SimpMessageType.SUBSCRIBE).denyAll()
        // .anyMessage().denyAll();
        // TODO what is wrong with these?
        .anyMessage().permitAll();//.authenticated();
  }

  @Override
  protected boolean sameOriginDisabled() {
    return true;
  }
}