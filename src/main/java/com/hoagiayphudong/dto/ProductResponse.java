package com.hoagiayphudong.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.hoagiayphudong.model.Category;
import com.hoagiayphudong.model.InventoryStatus;
import com.hoagiayphudong.model.Product;

public record ProductResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String slug,
        String code,
        String shortDescription,
        BigDecimal priceAmount,
        String priceLabel,
        String colorFamily,
        Integer heightCm,
        InventoryStatus inventoryStatus,
        String inventoryStatusLabel,
        String thumbnailUrl,
        Boolean featured,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static ProductResponse from(Product product) {
        Category category = product.getCategory();

        return new ProductResponse(
                product.getId(),
                category == null ? null : category.getId(),
                category == null ? null : category.getName(),
                product.getName(),
                product.getSlug(),
                product.getCode(),
                product.getShortDescription(),
                product.getPriceAmount(),
                product.getPriceLabel(),
                product.getColorFamily(),
                product.getHeightCm(),
                product.getInventoryStatus(),
                inventoryStatusLabel(product.getInventoryStatus()),
                product.getThumbnailUrl(),
                product.getFeatured(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private static String inventoryStatusLabel(InventoryStatus status) {
        if (status == null) {
            return "";
        }

        return switch (status) {
            case AVAILABLE -> "Còn hàng";
            case OUT_OF_STOCK -> "Hết hàng";
            case PRE_ORDER -> "Đặt trước";
        };
    }
}
