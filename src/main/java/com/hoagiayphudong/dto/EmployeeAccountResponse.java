package com.hoagiayphudong.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.hoagiayphudong.model.Manager;
import com.hoagiayphudong.model.Role;
import com.hoagiayphudong.model.User;

public record EmployeeAccountResponse(
        Long id,
        Long managerId,
        String managerName,
        String username,
        String email,
        List<Long> roleIds,
        List<String> roles,
        List<String> roleLabels,
        Boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static EmployeeAccountResponse from(Manager manager) {
        User user = manager.getUser();
        List<Role> roles = user.getRoles();

        return new EmployeeAccountResponse(
                user.getId(),
                manager.getId(),
                manager.getFullName(),
                user.getUsername(),
                user.getEmail(),
                roles.stream().map(Role::getId).toList(),
                roles.stream().map(Role::getName).toList(),
                roles.stream().map(EmployeeAccountResponse::roleLabel).toList(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private static String roleLabel(Role role) {
        return role.getDescription() == null || role.getDescription().isBlank()
                ? role.getName()
                : role.getDescription();
    }
}
