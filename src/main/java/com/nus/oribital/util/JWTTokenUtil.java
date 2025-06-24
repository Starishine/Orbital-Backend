package com.nus.oribital.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JWTTokenUtil {

    private static final String JWT_SECRET = "NUSOribital!2025Semester2HappyCoding"; // Use a strong secret in production
    private static final long JWT_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    /**
     * Utiluty method to generate a JWT token for a given username.
     *
     * @param username
     * @return
     */
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

    }

    /**
     * Utility method to validate a JWT token.
     *
     * @param token
     * @return
     */
    public String getUser(String token) {
        try {
            Claims claims = decodeJwtToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null; // Token is invalid or expired
        }

    }

    /**
     * Utility method to decode a JWT token and extract claims.
     *
     * @param token
     * @return
     */
    private Claims decodeJwtToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .build();
        return (Claims) parser.parseSignedClaims(token).getPayload();
    }
}
