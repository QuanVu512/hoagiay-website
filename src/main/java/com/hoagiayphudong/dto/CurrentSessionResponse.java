package com.hoagiayphudong.dto;

public record CurrentSessionResponse(
        boolean loggedIn,
        String username,
        boolean admin
) {
}
