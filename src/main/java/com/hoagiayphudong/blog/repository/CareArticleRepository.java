package com.hoagiayphudong.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hoagiayphudong.blog.model.CareArticle;

public interface CareArticleRepository extends JpaRepository<CareArticle, Long> {

    List<CareArticle> findByPublishedTrueOrderByPublishedAtDescCreatedAtDesc();

    Optional<CareArticle> findBySlugAndPublishedTrue(String slug);
}
