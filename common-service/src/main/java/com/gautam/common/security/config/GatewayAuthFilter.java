package com.gautam.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayAuthFilter extends OncePerRequestFilter {

    private final SecurityProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip filter for public endpoints
        return path.startsWith("/api/auth") ||
                path.startsWith("/actuator") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(properties.getInternal().getHeader());

        if (!properties.getInternal().getSecret().equals(header)) {
            log.warn("Unauthorized internal access attempt to {} from {}",
                    request.getRequestURI(), request.getRemoteAddr());

            ApiError error = ApiError.of(
                    HttpStatus.UNAUTHORIZED.value(),
                    "UNAUTHORIZED",
                    "Unauthorized internal access",
                    "This is an internal service and cannot be accessed directly. " +
                            "Include the correct 'X-Internal-Auth' header with a valid secret.",
                    request.getRequestURI(),
                    request.getMethod()
            );

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(error));
            return;
        }

        // Clear any existing authentication
        SecurityContextHolder.clearContext();
        filterChain.doFilter(request, response);
    }
}
