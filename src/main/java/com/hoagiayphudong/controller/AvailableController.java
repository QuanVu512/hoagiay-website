package com.hoagiayphudong.controller;

import java.util.List;

import com.hoagiayphudong.helper.response.ApiResponse;
import com.hoagiayphudong.dto.ManagerOptionResponse;
import com.hoagiayphudong.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/available")
@RequiredArgsConstructor
public class AvailableController {

    private final ManagerService managerService;

    @GetMapping("/manager")
    public ApiResponse<List<ManagerOptionResponse>> findManagersWithoutAccount() {
        return ApiResponse.success("Lấy danh sách hồ sơ chưa có tài khoản thành công", managerService.findManagersWithoutAccount());
    }
}
