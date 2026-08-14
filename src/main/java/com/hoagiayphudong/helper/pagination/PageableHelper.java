package com.hoagiayphudong.helper.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableHelper {

    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private PageableHelper() {
    }

    public static Pageable normalize(Pageable pageable, Sort defaultSort) {
        int page = pageable == null || pageable.isUnpaged()
                ? 0
                : Math.max(pageable.getPageNumber(), 0);
        int size = pageable == null || pageable.isUnpaged()
                ? DEFAULT_SIZE
                : Math.max(1, Math.min(pageable.getPageSize(), MAX_SIZE));
        Sort sort = pageable != null && pageable.getSort().isSorted()
                ? pageable.getSort()
                : defaultSort;

        return PageRequest.of(page, size, sort == null ? Sort.unsorted() : sort);
    }
}
