package com.hoagiayphudong.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        int numberOfElements,
        boolean first,
        boolean last,
        boolean empty,
        List<String> sort
) {

    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        Page<T> mappedPage = page.map(mapper);
        return new PageResponse<>(
                mappedPage.getContent(),
                mappedPage.getNumber(),
                mappedPage.getSize(),
                mappedPage.getTotalElements(),
                mappedPage.getTotalPages(),
                mappedPage.getNumberOfElements(),
                mappedPage.isFirst(),
                mappedPage.isLast(),
                mappedPage.isEmpty(),
                toSortValues(page.getSort())
        );
    }

    private static List<String> toSortValues(Sort sort) {
        return sort.stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .toList();
    }
}
