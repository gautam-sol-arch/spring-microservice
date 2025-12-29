package com.gautam.accounts.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gateway.security")
public class SecurityProperties {

    private String internalSecret;
}

