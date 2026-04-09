package com.example.sms.controller;

import com.example.sms.dto.CreateBriefingRequest;
import com.example.sms.model.BriefingTask;
import com.example.sms.service.MockDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class BriefingTaskController {

    private final MockDataService mockDataService;

    public BriefingTaskController(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    @GetMapping
    public List<BriefingTask> list() {
        return mockDataService.getTasks();
    }

    @PostMapping
    public BriefingTask create(@Valid @RequestBody CreateBriefingRequest request) {
        return mockDataService.createTask(request);
    }
}
