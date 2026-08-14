package com.hoagiayphudong.helper.specification;

import java.util.ArrayList;
import java.util.List;

import com.hoagiayphudong.model.Manager;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ManagerSpecification {

    private ManagerSpecification() {
    }

    public static Specification<Manager> filter(String keyword, Long departmentId, Boolean hasAccount) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (SpecificationText.hasText(keyword)) {
                String pattern = SpecificationText.likePattern(keyword);
                predicates.add(builder.or(
                        SpecificationText.like(builder, root.get("fullName"), pattern),
                        SpecificationText.like(builder, root.get("phone"), pattern),
                        SpecificationText.like(builder, root.get("addressDetail"), pattern),
                        SpecificationText.like(builder, root.get("ward"), pattern),
                        SpecificationText.like(builder, root.get("district"), pattern),
                        SpecificationText.like(builder, root.get("province"), pattern),
                        SpecificationText.like(builder, root.join("department", JoinType.LEFT).get("name"), pattern),
                        SpecificationText.like(builder, root.join("user", JoinType.LEFT).get("username"), pattern)
                ));
            }
            if (departmentId != null) {
                predicates.add(builder.equal(root.join("department", JoinType.LEFT).get("id"), departmentId));
            }
            if (hasAccount != null) {
                predicates.add(hasAccount ? builder.isNotNull(root.get("user")) : builder.isNull(root.get("user")));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
