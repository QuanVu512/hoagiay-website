package com.hoagiayphudong.dto;

import com.hoagiayphudong.model.Manager;

public record ManagerOptionResponse(
        Long id,
        String fullName,
        String label
) {

    public static ManagerOptionResponse from(Manager manager) {
        return new ManagerOptionResponse(
                manager.getId(),
                manager.getFullName(),
                manager.getId() + " - " + manager.getFullName()
        );
    }
}
