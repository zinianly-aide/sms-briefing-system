package com.example.sms.smstask.controller;
import com.example.sms.common.api.ApiResponse; import com.example.sms.smstask.entity.SmsTask; import com.example.sms.smstask.service.SmsTaskService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/tasks","/api/sms-tasks"})
public class SmsTaskController {
    private final SmsTaskService service;

    public SmsTaskController(SmsTaskService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<SmsTask>> list() {
        return ApiResponse.success(service.list());
    }
}
