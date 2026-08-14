package com.hoagiayphudong.helper.cookie;

import java.time.Duration;
import java.time.Instant;

import com.hoagiayphudong.config.CookieProperties;
import com.hoagiayphudong.dto.AuthLoginResponse;
import com.hoagiayphudong.dto.AuthTokenExchangeResponse;
import com.hoagiayphudong.helper.path.AuthPath;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthCookieHelper {

    public static final String ACCESS_TOKEN_COOKIE = "af";
    public static final String REFRESH_TOKEN_COOKIE = "rf";
    private static final String ACCESS_TOKEN_PATH = "/";
    private static final String REFRESH_TOKEN_PATH = AuthPath.BASE;
    private static final String DEFAULT_SAME_SITE = "Lax";

    private final CookieProperties cookieProperties;

    public void addAuthCookies(HttpServletResponse response, AuthLoginResponse data) {
        addCookies(
                response,
                data.accessToken(),
                data.expiresAt(),
                data.refreshToken(),
                data.refreshExpiresAt()
        );
    }

    public void addAuthCookies(HttpServletResponse response, AuthTokenExchangeResponse data) {
        addCookies(
                response,
                data.accessToken(),
                data.expiresAt(),
                data.refreshToken(),
                data.refreshExpiresAt()
        );
    }

    private void addCookies(
            HttpServletResponse response,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) {
        response.addHeader(HttpHeaders.SET_COOKIE, createCookie(
                ACCESS_TOKEN_COOKIE,
                accessToken,
                ACCESS_TOKEN_PATH,
                accessTokenExpiresAt
        ).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, createCookie(
                REFRESH_TOKEN_COOKIE,
                refreshToken,
                REFRESH_TOKEN_PATH,
                refreshTokenExpiresAt
        ).toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie(ACCESS_TOKEN_COOKIE, ACCESS_TOKEN_PATH).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie(REFRESH_TOKEN_COOKIE, REFRESH_TOKEN_PATH).toString());
    }

    private ResponseCookie createCookie(String name, String value, String path, Instant expiresAt) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecureCookie())
                .sameSite(sameSite())
                .path(path)
                .maxAge(maxAge(expiresAt))
                .build();
    }

    private ResponseCookie deleteCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(isSecureCookie())
                .sameSite(sameSite())
                .path(path)
                .maxAge(Duration.ZERO)
                .build();
    }

    private Duration maxAge(Instant expiresAt) {
        Duration maxAge = Duration.between(Instant.now(), expiresAt);
        return maxAge.isNegative() ? Duration.ZERO : maxAge;
    }

    private boolean isSecureCookie() {
        return Boolean.TRUE.equals(cookieProperties.secure());
    }

    private String sameSite() {
        if (cookieProperties.sameSite() == null || cookieProperties.sameSite().isBlank()) {
            return DEFAULT_SAME_SITE;
        }

        return cookieProperties.sameSite().trim();
    }
}
