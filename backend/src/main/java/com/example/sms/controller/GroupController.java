package com.example.sms.controller;

import com.example.sms.dto.CreateGroupRequest;
import com.example.sms.model.ContactGroup;
import com.example.sms.service.MockDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final MockDataService mockDataService;

    public GroupController(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    @GetMapping
    public List<ContactGroup> list() {
        return mockDataService.getGroups();
    }

    @PostMapping
    public ContactGroup create(@Valid @RequestBody CreateGroupRequest request) {
        return mockDataService.createGroup(request);
    }
}
