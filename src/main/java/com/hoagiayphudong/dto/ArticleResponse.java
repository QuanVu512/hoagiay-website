package com.hoagiayphudong.dto;

import java.time.OffsetDateTime;

import com.hoagiayphudong.model.CareArticle;

public record ArticleResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String content,
        String thumbnailUrl,
        Boolean published,
        OffsetDateTime publishedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static ArticleResponse from(CareArticle article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getContent(),
                article.getThumbnailUrl(),
                article.getPublished(),
                article.getPublishedAt(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
