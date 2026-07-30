package com.hoagiayphudong.dto;

import com.hoagiayphudong.model.Role;

public record RoleOptionResponse(
        Long id,
        String name,
        String description,
        String label
) {

    public static RoleOptionResponse from(Role role) {
        String label = role.getDescription() == null || role.getDescription().isBlank()
                ? role.getName()
                : role.getDescription();

        return new RoleOptionResponse(role.getId(), role.getName(), role.getDescription(), label);
    }
}
