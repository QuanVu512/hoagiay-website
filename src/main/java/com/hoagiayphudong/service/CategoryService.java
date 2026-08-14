package com.hoagiayphudong.service;

import java.util.List;

import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.dto.CategoryRequest;
import com.hoagiayphudong.dto.CategoryResponse;
import com.hoagiayphudong.helper.exception.ResourceAlreadyExistsException;
import com.hoagiayphudong.helper.exception.ResourceNotFoundException;
import com.hoagiayphudong.helper.pagination.PageableHelper;
import com.hoagiayphudong.helper.specification.CategorySpecification;
import com.hoagiayphudong.helper.text.SlugHelper;
import com.hoagiayphudong.model.Category;
import com.hoagiayphudong.repository.CategoryRepository;
import com.hoagiayphudong.repository.ProductRepository;
import com.hoagiayphudong.security.SecurityPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @PreAuthorize(SecurityPermission.CATEGORY)
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> findAllForAdmin(Pageable pageable, String keyword, Boolean active) {
        Pageable safePageable = PageableHelper.normalize(pageable, Sort.by(
                Sort.Order.asc("sortOrder"),
                Sort.Order.asc("id")
        ));
        return PageResponse.from(
                categoryRepository.findAll(CategorySpecification.filter(keyword, active), safePageable),
                CategoryResponse::from
        );
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findActiveForPublic() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> findActiveForPublic(Pageable pageable) {
        Pageable safePageable = PageableHelper.normalize(pageable, Sort.by(
                Sort.Order.asc("sortOrder"),
                Sort.Order.asc("id")
        ));
        return PageResponse.from(categoryRepository.findByActiveTrue(safePageable), CategoryResponse::from);
    }

    @PreAuthorize(SecurityPermission.CATEGORY)
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(findCategory(id));
    }

    @PreAuthorize(SecurityPermission.CATEGORY)
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String slug = resolveSlug(request.slug(), request.name());
        validateUniqueSlug(slug, null);

        Category category = new Category();
        copyRequestToCategory(request, category, slug);

        return CategoryResponse.from(categoryRepository.save(category));
    }

    @PreAuthorize(SecurityPermission.CATEGORY)
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findCategory(id);
        String slug = resolveSlug(request.slug(), request.name());
        validateUniqueSlug(slug, id);
        copyRequestToCategory(request, category, slug);

        return CategoryResponse.from(categoryRepository.save(category));
    }

    @PreAuthorize(SecurityPermission.CATEGORY)
    @Transactional
    public void delete(Long id) {
        Category category = findCategory(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Không thể xoá danh mục đang có sản phẩm.");
        }

        categoryRepository.delete(category);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục id " + id));
    }

    private void copyRequestToCategory(CategoryRequest request, Category category, String slug) {
        category.setName(cleanText(request.name()));
        category.setSlug(slug);
        category.setDescription(cleanText(request.description()));
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        category.setActive(request.active() == null || request.active());
    }

    private void validateUniqueSlug(String slug, Long ignoredCategoryId) {
        boolean exists = ignoredCategoryId == null
                ? categoryRepository.existsBySlugIgnoreCase(slug)
                : categoryRepository.existsBySlugIgnoreCaseAndIdNot(slug, ignoredCategoryId);

        if (exists) {
            throw new ResourceAlreadyExistsException("Slug danh mục đã tồn tại.");
        }
    }

    private String resolveSlug(String slug, String fallback) {
        return SlugHelper.normalizeSlug(hasText(slug) ? slug : fallback);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String cleanText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
