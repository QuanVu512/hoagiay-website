package com.hoagiayphudong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoagiayphudong.blog.dto.ArticleDetailResponse;
import com.hoagiayphudong.blog.dto.ArticleSummaryResponse;
import com.hoagiayphudong.repository.CareArticleRepository;
import com.hoagiayphudong.common.exception.ResourceNotFoundException;

@Service
public class CareArticleService {

    private final CareArticleRepository careArticleRepository;

    public CareArticleService(CareArticleRepository careArticleRepository) {
        this.careArticleRepository = careArticleRepository;
    }

    @Transactional(readOnly = true)
    public List<ArticleSummaryResponse> findPublishedArticles() {
        return careArticleRepository.findByPublishedTrueOrderByPublishedAtDescCreatedAtDesc()
                .stream()
                .map(ArticleSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArticleDetailResponse findBySlug(String slug) {
        return careArticleRepository.findBySlugAndPublishedTrue(slug)
                .map(ArticleDetailResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết: " + slug));
    }
}
