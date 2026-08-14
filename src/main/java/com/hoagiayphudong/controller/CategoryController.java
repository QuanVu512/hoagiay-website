package com.hoagiayphudong.controller;

import com.hoagiayphudong.dto.CategoryRequest;
import com.hoagiayphudong.dto.CategoryResponse;
import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.helper.response.ApiResponse;
import com.hoagiayphudong.service.CategoryService;
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
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<PageResponse<CategoryResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active
    ) {
        return ApiResponse.success("Lấy danh sách danh mục thành công", categoryService.findAllForAdmin(pageable, keyword, active));
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> findById(@PathVariable Long id) {
        return ApiResponse.success("Lấy danh mục thành công", categoryService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success("Tạo danh mục thành công", categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success("Cập nhật danh mục thành công", categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.success("Xoá danh mục thành công", null);
    }
}
