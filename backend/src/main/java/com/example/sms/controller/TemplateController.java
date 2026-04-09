package com.example.sms.controller;

import com.example.sms.dto.CreateTemplateRequest;
import com.example.sms.model.TemplateCard;
import com.example.sms.service.MockDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final MockDataService mockDataService;

    public TemplateController(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    @GetMapping
    public List<TemplateCard> list() {
        return mockDataService.getTemplates();
    }

    @PostMapping
    public TemplateCard create(@Valid @RequestBody CreateTemplateRequest request) {
        return mockDataService.createTemplate(request);
    }
}
