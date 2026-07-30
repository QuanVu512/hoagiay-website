package com.hoagiayphudong.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 120)
    @Setter
    private String fullName;

    @Column(length = 30)
    @Setter
    private String phone;

    @Column(length = 160)
    @Setter
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Setter
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    @Setter
    private ContactMessageStatus status = ContactMessageStatus.NEW;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void beforeCreate() {
        createdAt = OffsetDateTime.now();
    }
}
