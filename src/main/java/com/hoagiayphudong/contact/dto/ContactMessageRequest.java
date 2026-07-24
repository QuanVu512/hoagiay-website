package com.hoagiayphudong.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactMessageRequest(
        @NotBlank(message = "Vui lòng nhập họ tên")
        @Size(max = 120, message = "Họ tên tối đa 120 ký tự")
        String fullName,

        @Size(max = 30, message = "Số điện thoại tối đa 30 ký tự")
        String phone,

        @Email(message = "Email chưa đúng định dạng")
        @Size(max = 160, message = "Email tối đa 160 ký tự")
        String email,

        @NotBlank(message = "Vui lòng nhập nội dung cần tư vấn")
        @Size(max = 2000, message = "Nội dung tối đa 2000 ký tự")
        String message
) {
}
