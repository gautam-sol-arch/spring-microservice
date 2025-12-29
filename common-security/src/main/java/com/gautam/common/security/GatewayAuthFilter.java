package com.gautam.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class GatewayAuthFilter extends OncePerRequestFilter {

    private final SecurityProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("X-Internal-Auth");

        if (!properties.getInternalSecret().equals(header)) {
            log.warn("Unauthorized access attempt to {} from {}", request.getRequestURI(), request.getRemoteAddr());

            ApiError error = new ApiError(
                    401,
                    "UNAUTHORIZED",
                    "Unauthorized access. This service cannot be accessed directly.",
                    "Ensure your request includes the required 'X-Internal-Auth' header.",
                    request.getRequestURI(),
                    request.getMethod()
            );

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
            return;
        }

        // Set authentication for downstream security
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "internal-client",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
