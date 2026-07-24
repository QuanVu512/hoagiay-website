package com.hoagiayphudong.catalog.dto;

import java.math.BigDecimal;

import com.hoagiayphudong.model.Category;
import com.hoagiayphudong.model.InventoryStatus;
import com.hoagiayphudong.model.Product;

public record ProductDetailResponse(
        Long id,
        String name,
        String slug,
        String code,
        String shortDescription,
        BigDecimal priceAmount,
        String priceLabel,
        String colorFamily,
        Integer heightCm,
        InventoryStatus inventoryStatus,
        String thumbnailUrl,
        Boolean featured,
        Long categoryId,
        String categoryName,
        String categorySlug
) {

    public static ProductDetailResponse from(Product product) {
        Category category = product.getCategory();
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getCode(),
                product.getShortDescription(),
                product.getPriceAmount(),
                product.getPriceLabel(),
                product.getColorFamily(),
                product.getHeightCm(),
                product.getInventoryStatus(),
                product.getThumbnailUrl(),
                product.getFeatured(),
                category == null ? null : category.getId(),
                category == null ? null : category.getName(),
                category == null ? null : category.getSlug()
        );
    }
}
