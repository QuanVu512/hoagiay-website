package com.hoagiayphudong.controller;

import java.util.List;

import com.hoagiayphudong.dto.ArticleResponse;
import com.hoagiayphudong.dto.CategoryResponse;
import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.dto.ProductResponse;
import com.hoagiayphudong.service.ArticleService;
import com.hoagiayphudong.service.CategoryService;
import com.hoagiayphudong.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicContentController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ArticleService articleService;

    @GetMapping("/api/categories")
    public List<CategoryResponse> findActiveCategories() {
        return categoryService.findActiveForPublic();
    }

    @GetMapping("/api/products")
    public PageResponse<ProductResponse> findProducts(@RequestParam(required = false) Boolean featured, Pageable pageable) {
        return Boolean.TRUE.equals(featured)
                ? productService.findFeaturedForPublic(pageable)
                : productService.findAllForPublic(pageable);
    }

    @GetMapping("/api/articles")
    public PageResponse<ArticleResponse> findPublishedArticles(Pageable pageable) {
        return articleService.findPublishedForPublic(pageable);
    }
}
