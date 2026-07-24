package com.hoagiayphudong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hoagiayphudong.model.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}
