package com.gautam.api.gateway.security;

public record ApiError(int status, String error, String message) {}

