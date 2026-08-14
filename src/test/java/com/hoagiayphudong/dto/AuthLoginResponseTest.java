package com.hoagiayphudong.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthLoginResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void jsonResponseDoesNotExposeRawTokens() throws Exception {
        AuthLoginResponse response = new AuthLoginResponse(
                "Bearer",
                "access-token-value",
                3600,
                Instant.parse("2026-08-14T00:00:00Z"),
                "refresh-token-value",
                Instant.parse("2026-08-21T00:00:00Z"),
                1L,
                "admin",
                List.of("ADMIN")
        );

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("admin", "ADMIN");
        assertThat(json).doesNotContain("access-token-value", "refresh-token-value");
        assertThat(json).doesNotContain("accessToken", "refreshToken", "tokenType");
    }
}
