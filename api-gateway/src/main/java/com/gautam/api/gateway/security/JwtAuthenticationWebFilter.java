package com.gautam.api.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gautam.api.gateway.security.config.SecurityProperties;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtTokenValidator jwtTokenValidator;
    private final SecurityProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip JWT validation for actuator endpoints
        if (path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        // Skip JWT validation for auth endpoints but still set internal header
        if (path.startsWith("/api/auth")) {
            return chain.filter(addInternalHeader(exchange));
        }

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
            Claims claims = jwtTokenValidator.validateAndGetClaims(token);
            List<GrantedAuthority> authorities = jwtTokenValidator.getAuthorities(claims);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User", claims.getSubject())
                            .header(properties.getInternal().getHeader(), properties.getInternal().getSecret())
                            .build())
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    authorities
            );

            return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception e) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED,
                    "INVALID_TOKEN",
                    "Invalid or expired token: " + e.getMessage());
        }
    }

    private ServerWebExchange addInternalHeader(ServerWebExchange exchange) {
        return exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(properties.getInternal().getHeader(), properties.getInternal().getSecret())
                        .build())
                .build();
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String error, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> errorResponse = Map.of(
                "error", error,
                "message", message
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
