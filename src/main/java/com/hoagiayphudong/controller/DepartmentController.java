package com.hoagiayphudong.controller;

import java.util.List;

import com.hoagiayphudong.helper.response.ApiResponse;
import com.hoagiayphudong.dto.DepartmentRequest;
import com.hoagiayphudong.dto.DepartmentResponse;
import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.service.DepartmentService;
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
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ApiResponse<PageResponse<DepartmentResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active
    ) {
        return ApiResponse.success("Lấy danh sách bộ phận thành công", departmentService.findAll(pageable, keyword, active));
    }

    @GetMapping("/option")
    public ApiResponse<List<DepartmentResponse>> findOptions() {
        return ApiResponse.success("Lấy danh sách bộ phận thành công", departmentService.findAllOptions());
    }

    @GetMapping("/{id}")
    public ApiResponse<DepartmentResponse> findById(@PathVariable Long id) {
        return ApiResponse.success("Lấy bộ phận thành công", departmentService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        return ApiResponse.success("Tạo bộ phận thành công", departmentService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<DepartmentResponse> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return ApiResponse.success("Cập nhật bộ phận thành công", departmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ApiResponse.success("Xoá bộ phận thành công", null);
    }
}
