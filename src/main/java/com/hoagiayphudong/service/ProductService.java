package com.hoagiayphudong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hoagiayphudong.catalog.dto.ProductDetailResponse;
import com.hoagiayphudong.catalog.dto.ProductSummaryResponse;
import com.hoagiayphudong.model.Product;
import com.hoagiayphudong.repository.ProductRepository;
import com.hoagiayphudong.common.exception.ResourceNotFoundException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> findProducts(Boolean featured, String categorySlug, String keyword) {
        List<Product> products;

        if (Boolean.TRUE.equals(featured)) {
            products = productRepository.findByFeaturedTrueOrderByCreatedAtDesc();
        } else if (StringUtils.hasText(categorySlug)) {
            products = productRepository.findByCategory_SlugOrderByCreatedAtDesc(categorySlug);
        } else if (StringUtils.hasText(keyword)) {
            products = productRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
        } else {
            products = productRepository.findAllByOrderByCreatedAtDesc();
        }

        return products.stream()
                .map(ProductSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(ProductDetailResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + slug));
    }
}
