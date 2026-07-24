package com.hoagiayphudong.catalog.dto;

import java.math.BigDecimal;

import com.hoagiayphudong.catalog.model.Category;
import com.hoagiayphudong.catalog.model.InventoryStatus;
import com.hoagiayphudong.catalog.model.Product;

public record ProductSummaryResponse(
        Long id,
        String name,
        String slug,
        String code,
        String shortDescription,
        BigDecimal priceAmount,
        String priceLabel,
        String colorFamily,
        InventoryStatus inventoryStatus,
        String thumbnailUrl,
        Boolean featured,
        String categoryName,
        String categorySlug
) {

    public static ProductSummaryResponse from(Product product) {
        Category category = product.getCategory();
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getCode(),
                product.getShortDescription(),
                product.getPriceAmount(),
                product.getPriceLabel(),
                product.getColorFamily(),
                product.getInventoryStatus(),
                product.getThumbnailUrl(),
                product.getFeatured(),
                category == null ? null : category.getName(),
                category == null ? null : category.getSlug()
        );
    }
}
