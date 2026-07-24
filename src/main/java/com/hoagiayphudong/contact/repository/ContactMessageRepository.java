package com.hoagiayphudong.contact.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hoagiayphudong.contact.model.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}
