package com.gautam.common.security.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standardized API error response format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    /**
     * HTTP status code
     */
    private int status;

    /**
     * Error type/code
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Additional details or hints
     */
    private String hint;

    /**
     * Requested URI
     */
    private String path;

    /**
     * HTTP method used
     */
    private String method;

    /**
     * Timestamp when the error occurred
     */
    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();

    /**
     * Additional error details
     */
    private Map<String, Object> details;

    /**
     * Creates a new ApiError with current timestamp
     */
    public static ApiError of(int status, String error, String message, String path, String method) {
        return ApiError.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .method(method)
                .build();
    }

    /**
     * Creates a new ApiError with hint and current timestamp
     */
    public static ApiError of(int status, String error, String message, String hint, String path, String method) {
        return ApiError.builder()
                .status(status)
                .error(error)
                .message(message)
                .hint(hint)
                .path(path)
                .method(method)
                .build();
    }
}
