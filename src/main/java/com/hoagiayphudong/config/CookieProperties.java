package com.hoagiayphudong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie")
public record CookieProperties(
        Boolean secure,
        String sameSite
) {
}
