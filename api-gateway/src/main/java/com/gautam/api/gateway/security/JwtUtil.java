package com.gautam.api.gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public Claims validateAndGetClaims(String token) {

        try {
            return Jwts.parserBuilder()
                       .setSigningKey(key)
                       .build()
                       .parseClaimsJws(token)
                       .getBody();

        } catch (ExpiredJwtException e) {
            log.error("JWT expired");
            throw e;

        } catch (MalformedJwtException e) {
            log.error("Malformed JWT");
            throw e;

        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT");
            throw e;

        } catch (SecurityException e) {
            log.error("Invalid signature");
            throw e;

        } catch (IllegalArgumentException e) {
            log.error("Empty JWT claims");
            throw e;
        }
    }
}
