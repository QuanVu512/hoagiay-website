package com.hoagiayphudong.contact.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoagiayphudong.contact.dto.ContactMessageRequest;
import com.hoagiayphudong.contact.dto.ContactMessageResponse;
import com.hoagiayphudong.contact.model.ContactMessage;
import com.hoagiayphudong.contact.repository.ContactMessageRepository;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    @Transactional
    public ContactMessageResponse create(ContactMessageRequest request) {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setFullName(request.fullName());
        contactMessage.setPhone(request.phone());
        contactMessage.setEmail(request.email());
        contactMessage.setMessage(request.message());

        ContactMessage savedMessage = contactMessageRepository.save(contactMessage);
        return ContactMessageResponse.from(savedMessage);
    }
}
