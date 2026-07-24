package com.hoagiayphudong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hoagiayphudong.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByCreatedAtDesc();

    List<Product> findByFeaturedTrueOrderByCreatedAtDesc();

    List<Product> findByCategory_SlugOrderByCreatedAtDesc(String categorySlug);

    List<Product> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    Optional<Product> findBySlug(String slug);
}
