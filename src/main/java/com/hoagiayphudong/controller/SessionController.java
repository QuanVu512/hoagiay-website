package com.hoagiayphudong.controller;

import com.hoagiayphudong.dto.CurrentSessionResponse;
import com.hoagiayphudong.helper.response.ApiResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping
    public ApiResponse<CurrentSessionResponse> current(Authentication authentication) {
        boolean loggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        boolean admin = loggedIn && authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));

        return ApiResponse.success("Lấy trạng thái đăng nhập thành công",
                new CurrentSessionResponse(loggedIn, loggedIn ? authentication.getName() : null, admin));
    }
}
