package com.hoagiayphudong.catalog.dto;

import com.hoagiayphudong.model.Category;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description,
        Integer sortOrder
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getSortOrder()
        );
    }
}
