package com.hoagiayphudong.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 120, message = "Họ tên tối đa 120 ký tự")
        String fullName,

        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(max = 50, message = "Tên đăng nhập tối đa 50 ký tự")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email chưa đúng định dạng")
        @Size(max = 160, message = "Email tối đa 160 ký tự")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, max = 120, message = "Mật khẩu phải từ 6 đến 120 ký tự")
        String password,

        @NotBlank(message = "Mật khẩu nhập lại không được để trống")
        String confirmPassword,

        @Size(max = 30, message = "Số điện thoại tối đa 30 ký tự")
        String phone
) {
}
