package com.example.sms.group.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.common.dto.PageResult;
import com.example.sms.group.entity.ContactGroup;
import com.example.sms.group.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService service;

    public GroupController(GroupService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResult<ContactGroup>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(service.listPaged(page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ContactGroup> get(@PathVariable Long id) {
        ContactGroup group = service.getById(id);
        if (group == null) {
            return ApiResponse.error(404, "群组不存在");
        }
        return ApiResponse.success(group);
    }

    @PostMapping
    public ApiResponse<ContactGroup> create(@Valid @RequestBody ContactGroup group) {
        return ApiResponse.success(service.create(group));
    }

    @PutMapping("/{id}")
    public ApiResponse<ContactGroup> update(@PathVariable Long id, @Valid @RequestBody ContactGroup group) {
        ContactGroup payload = new ContactGroup(id, group.getName(), group.getOwnerDept(), group.getMemberCount(), group.getTags(), group.getLastSyncTime(), group.getStatus(), group.getCreatedAt(), group.getUpdatedAt());
        return ApiResponse.success(service.update(payload));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }

    @GetMapping("/search")
    public ApiResponse<PageResult<ContactGroup>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(service.searchPaged(keyword, page, pageSize));
    }
}
