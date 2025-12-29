package com.gautam.common;

import com.gautam.common.security.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class CommonSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonSecurityApplication.class, args);
    }

}
