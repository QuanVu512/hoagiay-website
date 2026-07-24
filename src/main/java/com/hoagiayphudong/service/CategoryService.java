package com.hoagiayphudong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoagiayphudong.catalog.dto.CategoryResponse;
import com.hoagiayphudong.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findActiveCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAscNameAsc()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
