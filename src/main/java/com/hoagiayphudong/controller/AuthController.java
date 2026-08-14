package com.hoagiayphudong.controller;

import com.hoagiayphudong.dto.AuthLoginRequest;
import com.hoagiayphudong.dto.AuthLoginResponse;
import com.hoagiayphudong.dto.AuthAccountResponse;
import com.hoagiayphudong.dto.AuthRefreshRequest;
import com.hoagiayphudong.dto.AuthRegisterRequest;
import com.hoagiayphudong.dto.AuthTokenExchangeResponse;
import com.hoagiayphudong.helper.cookie.AuthCookieHelper;
import com.hoagiayphudong.helper.path.AuthPath;
import com.hoagiayphudong.helper.response.ApiResponse;
import com.hoagiayphudong.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthPath.BASE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthCookieHelper authCookieHelper;

    @GetMapping("/account")
    public ApiResponse<AuthAccountResponse> account() {
        return ApiResponse.success("Lay thong tin tai khoan thanh cong", authService.currentAccount());
    }

    @PostMapping("/login")
    public ApiResponse<AuthLoginResponse> login(
            @Valid @RequestBody AuthLoginRequest request,
            HttpServletResponse response
    ) {
        AuthLoginResponse data = authService.login(request);
        authCookieHelper.addAuthCookies(response, data);
        return ApiResponse.success("Đăng nhập thành công", data);
    }

    @PostMapping("/register")
    public ApiResponse<AuthLoginResponse> register(
            @Valid @RequestBody AuthRegisterRequest request,
            HttpServletResponse response
    ) {
        AuthLoginResponse data = authService.register(request);
        authCookieHelper.addAuthCookies(response, data);
        return ApiResponse.success("Đăng ký tài khoản thành công", data);
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthTokenExchangeResponse> refresh(
            @RequestBody(required = false) AuthRefreshRequest request,
            @CookieValue(name = AuthCookieHelper.REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        AuthTokenExchangeResponse data = authService.refresh(resolveRefreshRequest(request, refreshToken));
        authCookieHelper.addAuthCookies(response, data);
        return ApiResponse.success("Lam moi token thanh cong", data);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestBody(required = false) AuthRefreshRequest request,
            @CookieValue(name = AuthCookieHelper.REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(resolveRefreshRequest(request, refreshToken).refreshToken());
        authCookieHelper.clearAuthCookies(response);
        return ApiResponse.success("Dang xuat thanh cong", null);
    }

    private AuthRefreshRequest resolveRefreshRequest(AuthRefreshRequest request, String refreshToken) {
        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            return request;
        }

        return new AuthRefreshRequest(refreshToken);
    }
}
