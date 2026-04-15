package com.example.sms.smstask.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.constant.DomainValueValidator;
import com.example.sms.common.dto.PageResult;
import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.entity.SmsTaskRecipient;
import com.example.sms.smstask.service.SmsTaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/tasks", "/api/sms-tasks"})
public class SmsTaskController {
    private final SmsTaskService service;

    public SmsTaskController(SmsTaskService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResult<SmsTask>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(service.listPaged(page, pageSize));
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
    public ApiResponse<SmsTask> create(@Valid @RequestBody SmsTask task) {
        DomainValueValidator.validateTaskStatus(task.getStatus());
        DomainValueValidator.validateChannel(task.getChannel());
        return ApiResponse.success(service.create(task));
    }

    @PutMapping("/{id}")
    public ApiResponse<SmsTask> update(@PathVariable Long id, @Valid @RequestBody SmsTask task) {
        DomainValueValidator.validateTaskStatus(task.getStatus());
        DomainValueValidator.validateChannel(task.getChannel());
        SmsTask payload = new SmsTask(id, task.getTitle(), task.getChannel(), task.getPlannedSendTime(), task.getStatus(), task.getRecipientCount(), task.getCreator(), task.getSuccessRate(), task.getCreatedAt(), task.getUpdatedAt());
        return ApiResponse.success(service.update(payload));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }

    @GetMapping("/search")
    public ApiResponse<PageResult<SmsTask>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(service.searchPaged(keyword, page, pageSize));
    }

    @PostMapping("/{id}/execute")
    public ApiResponse<String> executeTask(@PathVariable Long id) {
        service.executeTask(id);
        return ApiResponse.success("任务已执行");
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<String> cancelTask(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "";
        service.cancelTask(id, reason);
        return ApiResponse.success("任务已取消");
    }

    @GetMapping("/{id}/recipients")
    public ApiResponse<List<SmsTaskRecipient>> getRecipients(@PathVariable Long id) {
        return ApiResponse.success(service.getRecipients(id));
    }
}
