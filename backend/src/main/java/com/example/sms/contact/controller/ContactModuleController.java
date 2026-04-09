package com.example.sms.contact.controller;
import com.example.sms.common.api.ApiResponse; import com.example.sms.contact.entity.ContactEntity; import com.example.sms.contact.service.ContactService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contacts/v2")
public class ContactModuleController {
    private final ContactService service;

    public ContactModuleController(ContactService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<ContactEntity>> list() {
        return ApiResponse.success(service.list());
    }
}
