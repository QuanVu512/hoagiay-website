package com.hoagiayphudong.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AuthTokenExchangeResponse(
        @JsonIgnore
        String tokenType,
        @JsonIgnore
        String accessToken,
        long expiresIn,
        Instant expiresAt,
        @JsonIgnore
        String refreshToken,
        Instant refreshExpiresAt
) {
}
