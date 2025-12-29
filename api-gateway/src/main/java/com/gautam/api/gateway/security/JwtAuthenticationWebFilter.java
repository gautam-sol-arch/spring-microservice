package com.gautam.api.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    @Value("${gateway.security.internal-secret}")
    private String internalSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Skip JWT validation but still set internal header for /auth/**
        if (path.startsWith("/api/auth")) {
            ServerWebExchange mutatedExchange = exchange.mutate()
                                                        .request(exchange.getRequest().mutate()
                                                                         .header("X-Internal-Auth", internalSecret)
                                                                         .build())
                                                        .build();
            return chain.filter(mutatedExchange);
        }

        // For other endpoints, check JWT
        String authHeader = exchange.getRequest()
                                    .getHeaders()
                                    .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED,
                              "AUTH_HEADER_MISSING",
                              "Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateAndGetClaims(token);

            ServerWebExchange mutatedExchange = exchange.mutate()
                                                        .request(exchange.getRequest().mutate()
                                                                         .header("X-User", claims.getSubject())
                                                                         .header("X-Internal-Auth", internalSecret)
                                                                         .build())
                                                        .build();

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            return chain.filter(mutatedExchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (ExpiredJwtException e) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED,
                              "JWT_EXPIRED",
                              "JWT token has expired");

        } catch (MalformedJwtException e) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED,
                              "JWT_MALFORMED",
                              "Malformed JWT token");

        } catch (UnsupportedJwtException e) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED,
                              "JWT_UNSUPPORTED",
                              "Unsupported JWT token");

        } catch (SecurityException e) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED,
                              "JWT_SIGNATURE_INVALID",
                              "Invalid JWT signature");

        } catch (IllegalArgumentException e) {
            return writeError(exchange, HttpStatus.BAD_REQUEST,
                              "JWT_EMPTY",
                              "JWT token is empty");
        }
    }


    private Mono<Void> writeError(ServerWebExchange exchange,
            HttpStatus status,
            String error,
            String message) {

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiError apiError = new ApiError(
                status.value(),
                error,
                message
        );

        byte[] bytes;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(apiError);
        } catch (Exception e) {
            bytes = message.getBytes();
        }

        DataBuffer buffer = exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
