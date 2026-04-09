package com.example.sms.group.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.group.entity.ContactGroup;
import com.example.sms.group.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService service;

    public GroupController(GroupService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<ContactGroup>> list() {
        return ApiResponse.success(service.listAll());
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
    public ApiResponse<ContactGroup> create(@RequestBody ContactGroup group) {
        return ApiResponse.success(service.create(group));
    }

    @PutMapping("/{id}")
    public ApiResponse<ContactGroup> update(@PathVariable Long id, @RequestBody ContactGroup group) {
        ContactGroup payload = new ContactGroup(id, group.name(), group.ownerDept(), group.memberCount(), group.tags(), group.lastSyncTime(), group.status(), group.createdAt(), group.updatedAt());
        return ApiResponse.success(service.update(payload));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(service.delete(id));
    }

    @GetMapping("/search")
    public ApiResponse<List<ContactGroup>> search(@RequestParam String keyword) {
        return ApiResponse.success(service.search(keyword));
    }
}
