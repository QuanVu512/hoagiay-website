package com.hoagiayphudong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Tên danh mục không được để trống")
        @Size(max = 120, message = "Tên danh mục tối đa 120 ký tự")
        String name,

        @Size(max = 160, message = "Slug tối đa 160 ký tự")
        String slug,

        String description,

        @Min(value = 0, message = "Thứ tự không được âm")
        Integer sortOrder,

        Boolean active
) {
}
