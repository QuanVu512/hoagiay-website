package com.hoagiayphudong.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeAccountUpdateRequest(
        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(max = 50, message = "Tên đăng nhập tối đa 50 ký tự")
        String username,

        String password,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email chưa đúng định dạng")
        @Size(max = 160, message = "Email tối đa 160 ký tự")
        String email,

        @NotEmpty(message = "Vui lòng chọn ít nhất một vai trò")
        Set<@NotNull(message = "Vai trò không hợp lệ") Long> roleIds,

        Boolean active
) {
}
