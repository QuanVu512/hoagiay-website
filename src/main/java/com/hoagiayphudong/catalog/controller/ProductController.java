package com.hoagiayphudong.catalog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hoagiayphudong.catalog.dto.ProductDetailResponse;
import com.hoagiayphudong.catalog.dto.ProductSummaryResponse;
import com.hoagiayphudong.catalog.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductSummaryResponse> findProducts(
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String q
    ) {
        return productService.findProducts(featured, categorySlug, q);
    }

    @GetMapping("/{slug}")
    public ProductDetailResponse findBySlug(@PathVariable String slug) {
        return productService.findBySlug(slug);
    }
}
