package com.vendingcom.auth_service.util.security;

import com.vendingcom.auth_service.application.dto.response.AuthRoleResponse;
import com.vendingcom.auth_service.domain.model.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final Long expirationMinutes;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") Long expirationMinutes
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(AuthUser user, List<AuthRoleResponse> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationMinutes * 60);

        List<String> roleCodes = roles.stream()
                .map(AuthRoleResponse::roleCode)
                .toList();

        return Jwts.builder()
                .subject(user.username())
                .claim("userId", user.userId())
                .claim("email", user.email())
                .claim("fullName", user.fullName())
                .claim("roles", roleCodes)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();

            return expiration != null && expiration.after(new Date());
        } catch (Exception exception) {
            return false;
        }
    }

    public Long getExpirationSeconds() {
        return expirationMinutes * 60;
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractClaims(token);
        Object rolesObject = claims.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .map(Object::toString)
                .toList();
    }
}