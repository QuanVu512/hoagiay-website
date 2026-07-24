package com.hoagiayphudong.blog.dto;

import java.time.OffsetDateTime;

import com.hoagiayphudong.blog.model.CareArticle;

public record ArticleDetailResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String content,
        String thumbnailUrl,
        OffsetDateTime publishedAt
) {

    public static ArticleDetailResponse from(CareArticle article) {
        return new ArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getContent(),
                article.getThumbnailUrl(),
                article.getPublishedAt()
        );
    }
}
