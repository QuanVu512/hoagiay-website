package com.hoagiayphudong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManagerRequest(
        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 120, message = "Họ tên tối đa 120 ký tự")
        String fullName,

        @Size(max = 30, message = "Số điện thoại tối đa 30 ký tự")
        String phone,

        Long departmentId,

        @Size(max = 160, message = "Địa chỉ chi tiết tối đa 160 ký tự")
        String addressDetail,

        @Size(max = 80, message = "Phường/xã tối đa 80 ký tự")
        String ward,

        @Size(max = 80, message = "Quận/huyện tối đa 80 ký tự")
        String district,

        @Size(max = 80, message = "Tỉnh/thành phố tối đa 80 ký tự")
        String province
) {
}
