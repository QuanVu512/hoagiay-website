package com.hoagiayphudong.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hoagiayphudong.common.response.ApiResponse;
import com.hoagiayphudong.contact.dto.ContactMessageRequest;
import com.hoagiayphudong.contact.dto.ContactMessageResponse;
import com.hoagiayphudong.service.ContactMessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contact-messages")
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    public ContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContactMessageResponse> create(@Valid @RequestBody ContactMessageRequest request) {
        ContactMessageResponse response = contactMessageService.create(request);
        return ApiResponse.success("Đã nhận thông tin tư vấn", response);
    }
}
