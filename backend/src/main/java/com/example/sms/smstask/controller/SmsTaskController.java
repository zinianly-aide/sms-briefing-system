package com.example.sms.smstask.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.service.SmsTaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/tasks", "/api/sms-tasks"})
public class SmsTaskController {
    private final SmsTaskService service;

    public SmsTaskController(SmsTaskService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<SmsTask>> list() {
        return ApiResponse.success(service.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<SmsTask> get(@PathVariable Long id) {
        SmsTask task = service.getById(id);
        if (task == null) {
            return ApiResponse.error(404, "发送任务不存在");
        }
        return ApiResponse.success(task);
    }

    @PostMapping
    public ApiResponse<SmsTask> create(@RequestBody SmsTask task) {
        return ApiResponse.success(service.create(task));
    }

    @PutMapping("/{id}")
    public ApiResponse<SmsTask> update(@PathVariable Long id, @RequestBody SmsTask task) {
        SmsTask payload = new SmsTask(id, task.title(), task.channel(), task.plannedSendTime(), task.status(), task.recipientCount(), task.creator(), task.successRate(), task.createdAt(), task.updatedAt());
        return ApiResponse.success(service.update(payload));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }

    @GetMapping("/search")
    public ApiResponse<List<SmsTask>> search(@RequestParam String keyword) {
        return ApiResponse.success(service.search(keyword));
    }
}
