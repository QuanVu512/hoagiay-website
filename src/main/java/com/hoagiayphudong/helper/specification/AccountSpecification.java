package com.hoagiayphudong.helper.specification;

import java.util.ArrayList;
import java.util.List;

import com.hoagiayphudong.model.Manager;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class AccountSpecification {

    private AccountSpecification() {
    }

    public static Specification<Manager> filter(String keyword, Long roleId, Boolean active) {
        return (root, query, builder) -> {
            if (query != null) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();
            Join<Object, Object> userJoin = root.join("user", JoinType.INNER);

            predicates.add(builder.isNotNull(root.get("user")));

            if (SpecificationText.hasText(keyword)) {
                String pattern = SpecificationText.likePattern(keyword);
                predicates.add(builder.or(
                        SpecificationText.like(builder, root.get("fullName"), pattern),
                        SpecificationText.like(builder, root.get("phone"), pattern),
                        SpecificationText.like(builder, userJoin.get("username"), pattern),
                        SpecificationText.like(builder, userJoin.get("email"), pattern),
                        SpecificationText.like(builder, root.join("department", JoinType.LEFT).get("name"), pattern)
                ));
            }
            if (roleId != null) {
                Join<Object, Object> assignmentJoin = userJoin.join("roleAssignments", JoinType.INNER);
                Join<Object, Object> roleJoin = assignmentJoin.join("role", JoinType.INNER);
                predicates.add(builder.equal(roleJoin.get("id"), roleId));
            }
            if (active != null) {
                predicates.add(builder.equal(userJoin.get("active"), active));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
