package com.example.sms.controller;

import com.example.sms.dto.CreateContactRequest;
import com.example.sms.model.Contact;
import com.example.sms.service.MockDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final MockDataService mockDataService;

    public ContactController(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    @GetMapping
    public List<Contact> list() {
        return mockDataService.getContacts();
    }

    @PostMapping
    public Contact create(@Valid @RequestBody CreateContactRequest request) {
        return mockDataService.createContact(request);
    }
}
