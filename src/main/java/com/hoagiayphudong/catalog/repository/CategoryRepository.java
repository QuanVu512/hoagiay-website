package com.hoagiayphudong.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hoagiayphudong.catalog.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByActiveTrueOrderBySortOrderAscNameAsc();

    Optional<Category> findBySlugAndActiveTrue(String slug);
}
