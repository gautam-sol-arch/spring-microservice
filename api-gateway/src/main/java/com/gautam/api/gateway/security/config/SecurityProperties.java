package com.gautam.api.gateway.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private Jwt jwt = new Jwt();
    private Internal internal = new Internal();

    @Data
    public static class Jwt {
        private String secret;
        private String issuer = "gautam-spring-app";
    }

    @Data
    public static class Internal {
        private String header = "X-Internal-Auth";
        private String secret;
    }
}
