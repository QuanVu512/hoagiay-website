package com.hoagiayphudong.controller;

import java.util.List;

import com.hoagiayphudong.helper.response.ApiResponse;
import com.hoagiayphudong.dto.ManagerRequest;
import com.hoagiayphudong.dto.ManagerResponse;
import com.hoagiayphudong.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping
    public ApiResponse<List<ManagerResponse>> findAll() {
        return ApiResponse.success("Lấy danh sách hồ sơ nhân viên thành công", managerService.findAll());
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
