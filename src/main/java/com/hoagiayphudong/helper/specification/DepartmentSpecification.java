package com.hoagiayphudong.helper.specification;

import java.util.ArrayList;
import java.util.List;

import com.hoagiayphudong.model.Department;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class DepartmentSpecification {

    private DepartmentSpecification() {
    }

    public static Specification<Department> filter(String keyword, Boolean active) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (SpecificationText.hasText(keyword)) {
                String pattern = SpecificationText.likePattern(keyword);
                predicates.add(builder.or(
                        SpecificationText.like(builder, root.get("name"), pattern),
                        SpecificationText.like(builder, root.get("description"), pattern)
                ));
            }
            if (active != null) {
                predicates.add(builder.equal(root.get("active"), active));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
