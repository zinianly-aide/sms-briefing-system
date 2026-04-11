package com.example.sms.group.controller;

import com.example.sms.common.api.ApiResponse;
import com.example.sms.group.entity.GroupMember;
import com.example.sms.group.service.GroupMemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    @GetMapping
    public ApiResponse<List<GroupMember>> list(@PathVariable Long groupId) {
        return ApiResponse.success(groupMemberService.getMembersByGroupId(groupId));
    }

    @PostMapping
    public ApiResponse<String> addMembers(@PathVariable Long groupId, @RequestBody Map<String, List<Long>> body) {
        List<Long> contactIds = body.get("contactIds");
        if (contactIds == null || contactIds.isEmpty()) {
            return ApiResponse.error(400, "联系人ID列表不能为空");
        }
        groupMemberService.addMembers(groupId, contactIds);
        return ApiResponse.success("添加成功");
    }

    @DeleteMapping("/{contactId}")
    public ApiResponse<String> removeMember(@PathVariable Long groupId, @PathVariable Long contactId) {
        groupMemberService.removeMember(groupId, contactId);
        return ApiResponse.success("移除成功");
    }
}
