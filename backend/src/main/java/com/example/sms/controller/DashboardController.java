package com.example.sms.controller;

import com.example.sms.dto.DashboardSummaryResponse;
import com.example.sms.service.MockDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final MockDataService mockDataService;

    public DashboardController(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    @GetMapping
    public DashboardSummaryResponse summary() {
        return mockDataService.getDashboard();
    }
}
