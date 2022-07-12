package com.servustech.eduson.features.products.webinars;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import com.servustech.eduson.security.jwt.JwtTokenProvider;

import org.springframework.security.oauth2.jwt.Jwt;

@Service
@AllArgsConstructor
public class MyJwtDecoder implements JwtDecoder {
  private final JwtTokenProvider tokenProvider;

  @Override
  public Jwt decode(String token) throws JwtException {
    if (tokenProvider.validateToken(token)) {
      // return tokenProvider.decode(token);
      return new Jwt(token, null, null, tokenProvider.getClaims(token), tokenProvider.getClaims(token));
    }
    return null;

  }

}
