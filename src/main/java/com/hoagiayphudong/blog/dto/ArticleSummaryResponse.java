package com.hoagiayphudong.blog.dto;

import java.time.OffsetDateTime;

import com.hoagiayphudong.model.CareArticle;

public record ArticleSummaryResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String thumbnailUrl,
        OffsetDateTime publishedAt
) {

    public static ArticleSummaryResponse from(CareArticle article) {
        return new ArticleSummaryResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getThumbnailUrl(),
                article.getPublishedAt()
        );
    }
}
