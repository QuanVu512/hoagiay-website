package com.hoagiayphudong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank(message = "Tên bộ phận không được để trống")
        @Size(max = 80, message = "Tên bộ phận tối đa 80 ký tự")
        String name,

        @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
        String description,

        Boolean active
) {
}
