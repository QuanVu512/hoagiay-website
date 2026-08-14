package com.hoagiayphudong.dto;

import java.time.OffsetDateTime;

import com.hoagiayphudong.model.Category;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description,
        Integer sortOrder,
        Boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getSortOrder(),
                category.getActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
