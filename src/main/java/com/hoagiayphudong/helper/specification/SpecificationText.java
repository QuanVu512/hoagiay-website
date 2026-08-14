package com.hoagiayphudong.helper.specification;

import java.util.Locale;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.util.StringUtils;

final class SpecificationText {

    private SpecificationText() {
    }

    static boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

    static String likePattern(String value) {
        return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
    }

    static Predicate like(CriteriaBuilder builder, Expression<String> expression, String pattern) {
        return builder.like(builder.lower(expression), pattern);
    }
}
