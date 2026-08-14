package com.hoagiayphudong.dto;

import java.math.BigDecimal;

import com.hoagiayphudong.model.InventoryStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        Long categoryId,

        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(max = 180, message = "Tên sản phẩm tối đa 180 ký tự")
        String name,

        @Size(max = 220, message = "Slug tối đa 220 ký tự")
        String slug,

        @Size(max = 40, message = "Mã sản phẩm tối đa 40 ký tự")
        String code,

        String shortDescription,

        @DecimalMin(value = "0.00", message = "Giá sản phẩm không được âm")
        BigDecimal priceAmount,

        @Size(max = 80, message = "Nhãn giá tối đa 80 ký tự")
        String priceLabel,

        @Size(max = 80, message = "Nhóm màu tối đa 80 ký tự")
        String colorFamily,

        @Min(value = 0, message = "Chiều cao không được âm")
        Integer heightCm,

        InventoryStatus inventoryStatus,

        @Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
        String thumbnailUrl,

        Boolean featured
) {
}
