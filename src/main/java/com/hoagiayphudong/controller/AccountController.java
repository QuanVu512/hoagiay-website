package com.hoagiayphudong.controller;

import java.util.List;

import com.hoagiayphudong.helper.response.ApiResponse;
import com.hoagiayphudong.dto.EmployeeAccountCreateRequest;
import com.hoagiayphudong.dto.EmployeeAccountResponse;
import com.hoagiayphudong.dto.EmployeeAccountUpdateRequest;
import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.dto.RoleOptionResponse;
import com.hoagiayphudong.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponse<EmployeeAccountResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Boolean active
    ) {
        return ApiResponse.success("Lấy danh sách tài khoản nhân viên thành công", userService.findAllAccounts(pageable, keyword, roleId, active));
    }

    @GetMapping("/role")
    public ApiResponse<List<RoleOptionResponse>> findRoles() {
        return ApiResponse.success("Lấy danh sách vai trò thành công", userService.findRoleOptions());
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeAccountResponse> findById(@PathVariable Long id) {
        return ApiResponse.success("Lấy tài khoản nhân viên thành công", userService.findAccountById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeAccountResponse> create(@Valid @RequestBody EmployeeAccountCreateRequest request) {
        return ApiResponse.success("Tạo tài khoản nhân viên thành công", userService.createAccount(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeAccountResponse> update(@PathVariable Long id, @Valid @RequestBody EmployeeAccountUpdateRequest request) {
        return ApiResponse.success("Cập nhật tài khoản nhân viên thành công", userService.updateAccount(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteAccount(id);
        return ApiResponse.success("Xoá tài khoản nhân viên thành công", null);
    }
}
