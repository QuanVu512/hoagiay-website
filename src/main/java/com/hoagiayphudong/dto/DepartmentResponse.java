package com.hoagiayphudong.dto;

import java.time.OffsetDateTime;

import com.hoagiayphudong.model.Department;

public record DepartmentResponse(
        Long id,
        String name,
        String description,
        Boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getActive(),
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }
}
