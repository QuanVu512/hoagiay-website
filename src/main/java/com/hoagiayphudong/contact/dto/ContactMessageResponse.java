package com.hoagiayphudong.contact.dto;

import java.time.OffsetDateTime;

import com.hoagiayphudong.contact.model.ContactMessage;
import com.hoagiayphudong.contact.model.ContactMessageStatus;

public record ContactMessageResponse(
        Long id,
        String fullName,
        String phone,
        String email,
        String message,
        ContactMessageStatus status,
        OffsetDateTime createdAt
) {

    public static ContactMessageResponse from(ContactMessage contactMessage) {
        return new ContactMessageResponse(
                contactMessage.getId(),
                contactMessage.getFullName(),
                contactMessage.getPhone(),
                contactMessage.getEmail(),
                contactMessage.getMessage(),
                contactMessage.getStatus(),
                contactMessage.getCreatedAt()
        );
    }
}
