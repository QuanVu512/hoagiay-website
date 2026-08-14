package com.hoagiayphudong.controller;

import com.hoagiayphudong.helper.response.ApiResponse;
import com.hoagiayphudong.dto.ManagerRequest;
import com.hoagiayphudong.dto.ManagerResponse;
import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping
    public ApiResponse<PageResponse<ManagerResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Boolean hasAccount
    ) {
        return ApiResponse.success("Lấy danh sách hồ sơ nhân viên thành công", managerService.findAll(pageable, keyword, departmentId, hasAccount));
    }

    @GetMapping("/{id}")
    public ApiResponse<ManagerResponse> findById(@PathVariable Long id) {
        return ApiResponse.success("Lấy hồ sơ nhân viên thành công", managerService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ManagerResponse> create(@Valid @RequestBody ManagerRequest request) {
        return ApiResponse.success("Tạo hồ sơ nhân viên thành công", managerService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ManagerResponse> update(@PathVariable Long id, @Valid @RequestBody ManagerRequest request) {
        return ApiResponse.success("Cập nhật hồ sơ nhân viên thành công", managerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        managerService.delete(id);
        return ApiResponse.success("Xoá hồ sơ nhân viên thành công", null);
    }
}
