package com.surofu.exporteru.application.utils;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
  @Value("${jwt.access-token.secret}")
  private String accessTokenSecret;
  @Value("${jwt.refresh-token.secret}")
  private String refreshTokenSecret;
  @Value("${jwt.access-token.lifetime}")
  private Duration accessTokenLifetime;
  @Value("${jwt.refresh-token.lifetime}")
  private Duration refreshTokenLifetime;

  public String generateAccessToken(UserDetails userDetails) {
    return generateToken(userDetails, accessTokenSecret, accessTokenLifetime);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateToken(userDetails, refreshTokenSecret, refreshTokenLifetime);
  }

  public Long extractUserIdFromAccessToken(String accessToken)
      throws JwtException, IllegalArgumentException {
    return extractClaimFromAccessToken(accessToken, claims -> claims.get("id", Long.class));
  }

  public UserEmail extractUserEmailFromAccessToken(String accessToken)
      throws JwtException, IllegalArgumentException {
    String rawEmail = extractClaimFromAccessToken(accessToken, Claims::getSubject);
    return new UserEmail(rawEmail);
  }

  public UserEmail extractUserEmailFromRefreshToken(String refreshToken)
      throws JwtException, IllegalArgumentException {
    String rawUserEmail = extractClaimFromRefreshToken(refreshToken, Claims::getSubject);
    return new UserEmail(rawUserEmail);
  }

  public UserRole extractUserRoleFromAccessToken(String accessToken)
      throws JwtException, IllegalArgumentException {
    String rawRole =
        extractClaimFromAccessToken(accessToken, claims -> claims.get("role", String.class));
    return UserRole.valueOf(rawRole.toUpperCase());
  }

  public <T> T extractClaimFromAccessToken(String accessToken, Function<Claims, T> claimsResolver)
      throws JwtException, IllegalArgumentException {
    final Claims claims = extractAllClaims(accessToken, accessTokenSecret);
    return claimsResolver.apply(claims);
  }

  public <T> T extractClaimFromRefreshToken(String refreshToken, Function<Claims, T> claimsResolver)
      throws JwtException, IllegalArgumentException {
    final Claims claims = extractAllClaims(refreshToken, refreshTokenSecret);
    return claimsResolver.apply(claims);
  }

  private String generateToken(UserDetails userDetails, String secret, Duration lifetime) {
    Map<String, Object> claims = new HashMap<>();

    if (userDetails instanceof SecurityUser securityUser) {
      claims.put("id", securityUser.getUser().getId());
      claims.put("role", securityUser.getUser().getRole().name());
    }

    Date issuedDate = new Date();
    Date expiresDate = new Date(issuedDate.getTime() + lifetime.toMillis());
    return Jwts.builder()
        .claims(claims)
        .subject(userDetails.getUsername())
        .issuedAt(issuedDate)
        .expiration(expiresDate)
        .signWith((getSignInKey(secret)))
        .compact();
  }

  private SecretKey getSignInKey(String secret) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims extractAllClaims(String token, String secret)
      throws JwtException, IllegalArgumentException {
    return Jwts.parser()
        .verifyWith(getSignInKey(secret))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
