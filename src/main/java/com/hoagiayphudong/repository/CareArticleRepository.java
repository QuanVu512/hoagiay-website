package com.hoagiayphudong.repository;

import com.hoagiayphudong.model.CareArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CareArticleRepository extends JpaRepository<CareArticle, Long>, JpaSpecificationExecutor<CareArticle> {

    Page<CareArticle> findAllBy(Pageable pageable);

    Page<CareArticle> findByPublishedTrue(Pageable pageable);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);
}
