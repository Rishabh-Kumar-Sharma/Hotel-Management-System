package com.learning.hotelManagementSystem.utils;

import com.learning.hotelManagementSystem.entity.User;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthUtil {
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.tokenExpirationTime}")
    private int tokenExpirationTime;

    private final SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserNameFromToken(String token) {
        Claims claims=getAllClaims(token);
        return claims.getSubject();
    }

    public String generateToken(User user) {
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("userId",user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*tokenExpirationTime))
                .signWith(getSecretKey())
                .compact();
    }

    public boolean isTokenValid(String token) {
        return getExpirationDateFromToken(token).after(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        Claims claims=getAllClaims(token);
        return claims.getExpiration();
    }
}
