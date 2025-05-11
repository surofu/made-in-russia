package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.application.security.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

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

    public Long extractIdFromAccessToken(String token) {
        return extractClaimFromAccessToken(token, claims -> claims.get("id", Long.class));
    }

    public Long extractIdFromRefreshToken(String token) {
        return extractClaimFromRefreshToken(token, claims -> claims.get("id", Long.class));
    }

    public String extractEmailFromAccessToken(String token) {
        return extractClaimFromAccessToken(token, Claims::getSubject);
    }

    public String extractEmailFromRefreshToken(String token) {
        return extractClaimFromRefreshToken(token, Claims::getSubject);
    }

    public List<?> extractRolesFromAccessToken(String token) {
        return extractClaimFromAccessToken(token, claims -> claims.get("roles", List.class));
    }

    public List<?> extractRolesFromRefreshToken(String token) {
        return extractClaimFromRefreshToken(token, claims -> claims.get("roles", List.class));
    }

    public <T> T extractClaimFromAccessToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, accessTokenSecret);
        return claimsResolver.apply(claims);
    }

    public <T> T extractClaimFromRefreshToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, refreshTokenSecret);
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

    private Claims extractAllClaims(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getSignInKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
