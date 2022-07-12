package com.servustech.eduson.security.jwt;

import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.payload.UserDetailsResponse;
import com.servustech.eduson.security.payload.StreamType;
import com.servustech.eduson.features.account.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Token Generator and user details provider class
 *
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.access-expiration-in-ms}")
    private int accessjwtExpirationInMs;

    @Value("${jwt.refresh-expiration-in-ms}")
    private int refreshJwtExpirationInMs;

    @Value("${jwt.video-expiration-in-ms}")
    private int videoJwtExpirationInMs;

    private final String secret;

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateRefreshToken(UserDetails userPrincipal) {
        return generateToken(userPrincipal, refreshJwtExpirationInMs);
    }

    public String generateAccessToken(UserDetails userPrincipal) {
        return generateToken(userPrincipal, accessjwtExpirationInMs);
    }

    public String generateVideoToken(
        User user,
        Long productId,
        ProductType productType,
        Long num,
        StreamType type
    ) {
        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + videoJwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("t", productType.isWebinar() ? 'w' : 'c');
        claims.put("p", productId);
        claims.put("st", type.isLive() ? 'l' : 'v');
        claims.put("cn", num);
        claims.put("r", RandomStringUtils.randomAlphanumeric(6));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private String generateToken(UserDetails userPrincipal, int expirationInMs) {

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expirationInMs);
        final String authorities = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put(AuthConstants.ROLES_KEY, authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUserNameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public UserDetailsResponse getUserNameAndRolesFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return new UserDetailsResponse(claims.getSubject(), claims.get(AuthConstants.ROLES_KEY).toString());
    }

    public Map<String, Object> getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            throw new MalformedJwtException("invalid-jwt-token");
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(null, null, "expired-jwt-token");
        } catch (UnsupportedJwtException ex) {
            throw new UnsupportedJwtException("unsupported-jwt-token");
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("jwt-claims-string-empty");
        }
    }

    public boolean willExpireSoon(String authToken) {
        var body = Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(authToken)
            .getBody();
            System.out.println("expiryDate " + body.toString());
        var expiryDate = body.getExpiration();// "8956786856376354523l";
        return (expiryDate.getTime() - (new Date()).getTime()) < refreshJwtExpirationInMs / 4;
    }
}
