package com.hoagiayphudong.helper.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.hoagiayphudong.model.Product;
import com.hoagiayphudong.model.InventoryStatus;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> filter(
            String keyword,
            Long categoryId,
            InventoryStatus inventoryStatus,
            Boolean featured,
            BigDecimal priceFrom,
            BigDecimal priceTo
    ) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (SpecificationText.hasText(keyword)) {
                String pattern = SpecificationText.likePattern(keyword);
                predicates.add(builder.or(
                        SpecificationText.like(builder, root.get("name"), pattern),
                        SpecificationText.like(builder, root.get("slug"), pattern),
                        SpecificationText.like(builder, root.get("code"), pattern),
                        SpecificationText.like(builder, root.get("shortDescription"), pattern),
                        SpecificationText.like(builder, root.get("colorFamily"), pattern),
                        SpecificationText.like(builder, root.join("category", JoinType.LEFT).get("name"), pattern)
                ));
            }
            if (categoryId != null) {
                predicates.add(builder.equal(root.join("category", JoinType.LEFT).get("id"), categoryId));
            }
            if (inventoryStatus != null) {
                predicates.add(builder.equal(root.get("inventoryStatus"), inventoryStatus));
            }
            if (featured != null) {
                predicates.add(builder.equal(root.get("featured"), featured));
            }
            if (priceFrom != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("priceAmount"), priceFrom));
            }
            if (priceTo != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("priceAmount"), priceTo));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
