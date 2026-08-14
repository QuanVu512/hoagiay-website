package com.hoagiayphudong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        String base64Secret,
        long validityInSeconds,
        long refreshTokenValidityInSeconds
) {
}
