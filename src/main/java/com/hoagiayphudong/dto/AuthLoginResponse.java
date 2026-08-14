package com.hoagiayphudong.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AuthLoginResponse(
        @JsonIgnore
        String tokenType,
        @JsonIgnore
        String accessToken,
        long expiresIn,
        Instant expiresAt,
        @JsonIgnore
        String refreshToken,
        Instant refreshExpiresAt,
        Long userId,
        String username,
        List<String> roles
) {
}
