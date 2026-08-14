package com.hoagiayphudong.service;

import java.time.OffsetDateTime;

import com.hoagiayphudong.dto.ArticleRequest;
import com.hoagiayphudong.dto.ArticleResponse;
import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.helper.exception.ResourceAlreadyExistsException;
import com.hoagiayphudong.helper.exception.ResourceNotFoundException;
import com.hoagiayphudong.helper.pagination.PageableHelper;
import com.hoagiayphudong.helper.specification.ArticleSpecification;
import com.hoagiayphudong.helper.text.SlugHelper;
import com.hoagiayphudong.model.CareArticle;
import com.hoagiayphudong.repository.CareArticleRepository;
import com.hoagiayphudong.security.SecurityPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final CareArticleRepository careArticleRepository;

    @PreAuthorize(SecurityPermission.ARTICLE)
    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> findAllForAdmin(Pageable pageable, String keyword, Boolean published) {
        Pageable safePageable = PageableHelper.normalize(pageable, Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.from(
                careArticleRepository.findAll(ArticleSpecification.filter(keyword, published), safePageable),
                ArticleResponse::from
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> findPublishedForPublic(Pageable pageable) {
        Pageable safePageable = PageableHelper.normalize(pageable, Sort.by(
                Sort.Order.desc("publishedAt"),
                Sort.Order.desc("id")
        ));
        return PageResponse.from(careArticleRepository.findByPublishedTrue(safePageable), ArticleResponse::from);
    }

    @PreAuthorize(SecurityPermission.ARTICLE)
    @Transactional(readOnly = true)
    public ArticleResponse findById(Long id) {
        return ArticleResponse.from(findArticle(id));
    }

    @PreAuthorize(SecurityPermission.ARTICLE)
    @Transactional
    public ArticleResponse create(ArticleRequest request) {
        String slug = resolveSlug(request.slug(), request.title());
        validateUniqueSlug(slug, null);

        CareArticle article = new CareArticle();
        copyRequestToArticle(request, article, slug);

        return ArticleResponse.from(careArticleRepository.save(article));
    }

    @PreAuthorize(SecurityPermission.ARTICLE)
    @Transactional
    public ArticleResponse update(Long id, ArticleRequest request) {
        CareArticle article = findArticle(id);
        String slug = resolveSlug(request.slug(), request.title());
        validateUniqueSlug(slug, id);
        copyRequestToArticle(request, article, slug);

        return ArticleResponse.from(careArticleRepository.save(article));
    }

    @PreAuthorize(SecurityPermission.ARTICLE)
    @Transactional
    public void delete(Long id) {
        careArticleRepository.delete(findArticle(id));
    }

    private CareArticle findArticle(Long id) {
        return careArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết id " + id));
    }

    private void copyRequestToArticle(ArticleRequest request, CareArticle article, String slug) {
        boolean published = request.published() != null && request.published();

        article.setTitle(cleanText(request.title()));
        article.setSlug(slug);
        article.setSummary(cleanText(request.summary()));
        article.setContent(cleanText(request.content()));
        article.setThumbnailUrl(cleanText(request.thumbnailUrl()));
        article.setPublished(published);
        article.setPublishedAt(resolvePublishedAt(article, published));
    }

    private OffsetDateTime resolvePublishedAt(CareArticle article, boolean published) {
        if (!published) {
            return null;
        }

        return article.getPublishedAt() == null ? OffsetDateTime.now() : article.getPublishedAt();
    }

    private void validateUniqueSlug(String slug, Long ignoredArticleId) {
        boolean exists = ignoredArticleId == null
                ? careArticleRepository.existsBySlugIgnoreCase(slug)
                : careArticleRepository.existsBySlugIgnoreCaseAndIdNot(slug, ignoredArticleId);

        if (exists) {
            throw new ResourceAlreadyExistsException("Slug bài viết đã tồn tại.");
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
