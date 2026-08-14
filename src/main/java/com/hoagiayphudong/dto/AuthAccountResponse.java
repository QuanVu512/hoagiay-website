package com.hoagiayphudong.dto;

import java.util.List;

public record AuthAccountResponse(
        Long userId,
        String username,
        List<String> roles,
        String managementPath
) {
}
