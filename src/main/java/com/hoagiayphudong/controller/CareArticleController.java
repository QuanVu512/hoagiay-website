package com.hoagiayphudong.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoagiayphudong.blog.dto.ArticleDetailResponse;
import com.hoagiayphudong.blog.dto.ArticleSummaryResponse;
import com.hoagiayphudong.service.CareArticleService;

@RestController
@RequestMapping("/api/articles")
public class CareArticleController {

    private final CareArticleService careArticleService;

    public CareArticleController(CareArticleService careArticleService) {
        this.careArticleService = careArticleService;
    }

    @GetMapping
    public List<ArticleSummaryResponse> findPublishedArticles() {
        return careArticleService.findPublishedArticles();
    }

    @GetMapping("/{slug}")
    public ArticleDetailResponse findBySlug(@PathVariable String slug) {
        return careArticleService.findBySlug(slug);
    }
}
