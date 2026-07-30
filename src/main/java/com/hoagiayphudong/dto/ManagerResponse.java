package com.hoagiayphudong.dto;

import java.time.OffsetDateTime;

import com.hoagiayphudong.model.Department;
import com.hoagiayphudong.model.Manager;
import com.hoagiayphudong.model.User;

public record ManagerResponse(
        Long id,
        String fullName,
        String phone,
        Long departmentId,
        String departmentName,
        String addressDetail,
        String ward,
        String district,
        String province,
        boolean hasAccount,
        Long accountId,
        String username,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static ManagerResponse from(Manager manager) {
        User user = manager.getUser();
        Department department = manager.getDepartment();

        return new ManagerResponse(
                manager.getId(),
                manager.getFullName(),
                manager.getPhone(),
                department == null ? null : department.getId(),
                department == null ? null : department.getName(),
                manager.getAddressDetail(),
                manager.getWard(),
                manager.getDistrict(),
                manager.getProvince(),
                user != null,
                user == null ? null : user.getId(),
                user == null ? null : user.getUsername(),
                manager.getCreatedAt(),
                manager.getUpdatedAt()
        );
    }
}
