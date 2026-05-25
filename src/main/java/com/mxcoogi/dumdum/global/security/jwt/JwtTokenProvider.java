package com.mxcoogi.dumdum.global.security.jwt;

import com.mxcoogi.dumdum.domain.auth.ClientType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long webRefreshTokenExpiry;
    private final long mobileRefreshTokenExpiry;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
            @Value("${jwt.web-refresh-token-expiry}") long webRefreshTokenExpiry,
            @Value("${jwt.mobile-refresh-token-expiry}") long mobileRefreshTokenExpiry) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(Base64.getEncoder().encodeToString(secret.getBytes())));
        this.accessTokenExpiry = accessTokenExpiry;
        this.webRefreshTokenExpiry = webRefreshTokenExpiry;
        this.mobileRefreshTokenExpiry = mobileRefreshTokenExpiry;
    }

    public String generateAccessToken(Long userId, String role) {
        return buildToken(userId, role, accessTokenExpiry);
    }

    public String generateRefreshToken(Long userId, ClientType clientType) {
        long expiry = clientType == ClientType.WEB ? webRefreshTokenExpiry : mobileRefreshTokenExpiry;
        return buildToken(userId, null, expiry);
    }

    public long getRefreshTokenExpiry(ClientType clientType) {
        return clientType == ClientType.WEB ? webRefreshTokenExpiry : mobileRefreshTokenExpiry;
    }

    private String buildToken(Long userId, String role, long expiry) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiry))
                .signWith(secretKey);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 JWT: {}", e.getMessage());
            return false;
        }
    }

    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
