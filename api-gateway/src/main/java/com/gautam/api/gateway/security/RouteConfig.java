package com.gautam.api.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    @Value("${gateway.security.internal-secret}")
    private String internalSecret;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                      // User Service
                      .route(
                              "user-service", r -> r.path("/user/**").filters(
                                      f -> f.addRequestHeader(
                                              "X-Internal-Auth",
                                              internalSecret)).uri(
                                      "http://localhost:8082"))
                      // Account Service
                      .route(
                              "account-service", r -> r.path("/api/account/**").filters(
                                      f -> f.addRequestHeader(
                                              "X-Internal-Auth",
                                              internalSecret)).uri(
                                      "http://localhost:8083"))
                      // Card Service
                      .route(
                              "card-service",
                              r -> r.path("/api/card/**").filters(f -> f.addRequestHeader(
                                      "X" +

                                              "-Internal-Auth", internalSecret)).uri(
                                      "http://localhost:8084"))
                      // Load Service
                      .route(
                              "loan-service",
                              r -> r.path("/api/loan/**").filters(f -> f.addRequestHeader(
                                      "X" +

                                              "-Internal-Auth", internalSecret)).uri(
                                      "http://localhost:8085")).build();
    }
}

