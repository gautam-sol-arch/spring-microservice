package com.gautam.common.security.exception;

import com.gautam.common.security.config.ApiError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.error("Authentication error: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_FAILED",
                "Authentication failed",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You don't have permission to access this resource",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    public ResponseEntity<ApiError> handleJwtException(RuntimeException ex, WebRequest request) {
        log.error("JWT validation error: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_TOKEN",
                "Invalid JWT token",
                "The provided token is invalid or malformed",
                request
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        log.error("JWT expired: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "TOKEN_EXPIRED",
                "JWT token has expired",
                "Please log in again to get a new token",
                request
        );
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiError> handleUnsupportedJwtException(UnsupportedJwtException ex, WebRequest request) {
        log.error("Unsupported JWT: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "UNSUPPORTED_TOKEN",
                "Unsupported JWT token",
                "The provided token is not supported",
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        log.error("Bad credentials: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Invalid username or password",
                "Please check your credentials and try again",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                "Please try again later or contact support",
                request
        );
    }

    private ResponseEntity<ApiError> buildErrorResponse(
            HttpStatus status,
            String errorCode,
            String message,
            String hint,
            WebRequest request) {

        String path = request.getDescription(false).replace("uri=", "");
        ApiError error = ApiError.of(
                status.value(),
                errorCode,
                message,
                hint,
                path,
                request.getHeader("X-Forwarded-Method") != null ?
                        request.getHeader("X-Forwarded-Method") : "GET"
        );

        return new ResponseEntity<>(error, status);
    }
}
