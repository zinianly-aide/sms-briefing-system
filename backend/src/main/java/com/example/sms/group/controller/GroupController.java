package com.example.sms.group.controller;
import com.example.sms.common.api.ApiResponse; import com.example.sms.group.entity.ContactGroup; import com.example.sms.group.service.GroupService;
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
        return ApiResponse.success(service.list());
    }
}
