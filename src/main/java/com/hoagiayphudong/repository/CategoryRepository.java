package com.hoagiayphudong.repository;

import java.util.List;

import com.hoagiayphudong.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Page<Category> findAllBy(Pageable pageable);

    List<Category> findByActiveTrueOrderBySortOrderAscIdAsc();

    Page<Category> findByActiveTrue(Pageable pageable);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);
}
