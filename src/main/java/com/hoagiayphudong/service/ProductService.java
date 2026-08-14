package com.hoagiayphudong.service;

import java.math.BigDecimal;

import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.dto.ProductRequest;
import com.hoagiayphudong.dto.ProductResponse;
import com.hoagiayphudong.helper.exception.ResourceAlreadyExistsException;
import com.hoagiayphudong.helper.exception.ResourceNotFoundException;
import com.hoagiayphudong.helper.pagination.PageableHelper;
import com.hoagiayphudong.helper.specification.ProductSpecification;
import com.hoagiayphudong.helper.text.SlugHelper;
import com.hoagiayphudong.model.Category;
import com.hoagiayphudong.model.InventoryStatus;
import com.hoagiayphudong.model.Product;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @PreAuthorize(SecurityPermission.PRODUCT)
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAllForAdmin(
            Pageable pageable,
            String keyword,
            Long categoryId,
            InventoryStatus inventoryStatus,
            Boolean featured,
            BigDecimal priceFrom,
            BigDecimal priceTo
    ) {
        Pageable safePageable = PageableHelper.normalize(pageable, Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.from(
                productRepository.findAll(ProductSpecification.filter(keyword, categoryId, inventoryStatus, featured, priceFrom, priceTo), safePageable),
                ProductResponse::from
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAllForPublic(Pageable pageable) {
        Pageable safePageable = PageableHelper.normalize(pageable, Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.from(productRepository.findAllBy(safePageable), ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findFeaturedForPublic(Pageable pageable) {
        Pageable safePageable = PageableHelper.normalize(pageable, Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.from(productRepository.findByFeaturedTrue(safePageable), ProductResponse::from);
    }

    @PreAuthorize(SecurityPermission.PRODUCT)
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(findProduct(id));
    }

    @PreAuthorize(SecurityPermission.PRODUCT)
    @Transactional
    public ProductResponse create(ProductRequest request) {
        String slug = resolveSlug(request.slug(), request.name());
        validateUniqueSlug(slug, null);

        Product product = new Product();
        copyRequestToProduct(request, product, slug);

        return ProductResponse.from(productRepository.save(product));
    }

    @PreAuthorize(SecurityPermission.PRODUCT)
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        String slug = resolveSlug(request.slug(), request.name());
        validateUniqueSlug(slug, id);
        copyRequestToProduct(request, product, slug);

        return ProductResponse.from(productRepository.save(product));
    }

    @PreAuthorize(SecurityPermission.PRODUCT)
    @Transactional
    public void delete(Long id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id " + id));
    }

    private void copyRequestToProduct(ProductRequest request, Product product, String slug) {
        product.setCategory(findCategory(request.categoryId()));
        product.setName(cleanText(request.name()));
        product.setSlug(slug);
        product.setCode(cleanText(request.code()));
        product.setShortDescription(cleanText(request.shortDescription()));
        product.setPriceAmount(request.priceAmount());
        product.setPriceLabel(hasText(request.priceLabel()) ? cleanText(request.priceLabel()) : "Liên hệ");
        product.setColorFamily(cleanText(request.colorFamily()));
        product.setHeightCm(request.heightCm());
        product.setInventoryStatus(request.inventoryStatus() == null ? InventoryStatus.AVAILABLE : request.inventoryStatus());
        product.setThumbnailUrl(cleanText(request.thumbnailUrl()));
        product.setFeatured(request.featured() != null && request.featured());
    }

    private Category findCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục id " + categoryId));
    }

    private void validateUniqueSlug(String slug, Long ignoredProductId) {
        boolean exists = ignoredProductId == null
                ? productRepository.existsBySlugIgnoreCase(slug)
                : productRepository.existsBySlugIgnoreCaseAndIdNot(slug, ignoredProductId);

        if (exists) {
            throw new ResourceAlreadyExistsException("Slug sản phẩm đã tồn tại.");
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
