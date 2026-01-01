package com.gautam.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private Jwt jwt = new Jwt();
    private Internal internal = new Internal();
    private Cors cors = new Cors();

    @Data
    public static class Jwt {
        private String secret;
        private long expirationMs = 86400000; // 24 hours
        private String issuer = "gautam-spring-app";
        private String header = "Authorization";
        private String tokenPrefix = "Bearer ";
    }

    @Data
    public static class Internal {
        private String header = "X-Internal-Auth";
        private String secret;
    }

    @Data
    public static class Cors {
        private String[] allowedOrigins = {"*"};
        private String[] allowedMethods = {"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"};
        private String[] allowedHeaders = {"*"};
        private String[] exposedHeaders = {"Authorization"};
        private boolean allowCredentials = false;
        private long maxAge = 3600L;
    }
}
