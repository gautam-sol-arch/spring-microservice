package com.gautam.users.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayAuthFilter extends OncePerRequestFilter {

    private final SecurityProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String header = request.getHeader("X-Internal-Auth");

        if (uri.startsWith("/api/auth")) {
            if (!properties.getInternalSecret().equals(header)) {
                log.warn("Unauthorized access attempt to {} from {}", uri, request.getRemoteAddr());
                sendUnauthorized(response);
                return;
            }
            log.info("Authorized /auth request from internal service to {}", uri);
        } else {
            // For all other endpoints, header must be present
            if (!properties.getInternalSecret().equals(header)) {
                log.warn("Unauthorized access attempt to {} from {}", uri, request.getRemoteAddr());
                sendUnauthorized(response);
                return;
            }
        }

        // Set authentication for downstream security checks
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "gateway",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_GATEWAY"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Proceed with the request
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String json = """
                {
                  "status": 401,
                  "error": "UNAUTHORIZED",
                  "message": "Access denied. This service cannot be accessed directly."
                }
                """;

        response.getWriter().write(json);
    }
}
