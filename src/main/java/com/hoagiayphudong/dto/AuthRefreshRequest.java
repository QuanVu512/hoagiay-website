package com.hoagiayphudong.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRefreshRequest(
        @NotBlank(message = "Refresh token khong duoc de trong")
        String refreshToken
) {
}
