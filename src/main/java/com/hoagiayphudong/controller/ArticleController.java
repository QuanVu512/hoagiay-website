package com.hoagiayphudong.controller;

import com.hoagiayphudong.dto.ArticleRequest;
import com.hoagiayphudong.dto.ArticleResponse;
import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.helper.response.ApiResponse;
import com.hoagiayphudong.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ApiResponse<PageResponse<ArticleResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean published
    ) {
        return ApiResponse.success("Lấy danh sách bài viết thành công", articleService.findAllForAdmin(pageable, keyword, published));
    }

    @GetMapping("/{id}")
    public ApiResponse<ArticleResponse> findById(@PathVariable Long id) {
        return ApiResponse.success("Lấy bài viết thành công", articleService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ArticleResponse> create(@Valid @RequestBody ArticleRequest request) {
        return ApiResponse.success("Tạo bài viết thành công", articleService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ArticleResponse> update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        return ApiResponse.success("Cập nhật bài viết thành công", articleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ApiResponse.success("Xoá bài viết thành công", null);
    }
}
