package com.hoagiayphudong.helper.specification;

import java.util.ArrayList;
import java.util.List;

import com.hoagiayphudong.model.CareArticle;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ArticleSpecification {

    private ArticleSpecification() {
    }

    public static Specification<CareArticle> filter(String keyword, Boolean published) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (SpecificationText.hasText(keyword)) {
                String pattern = SpecificationText.likePattern(keyword);
                predicates.add(builder.or(
                        SpecificationText.like(builder, root.get("title"), pattern),
                        SpecificationText.like(builder, root.get("slug"), pattern),
                        SpecificationText.like(builder, root.get("summary"), pattern),
                        SpecificationText.like(builder, root.get("content"), pattern)
                ));
            }
            if (published != null) {
                predicates.add(builder.equal(root.get("published"), published));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
