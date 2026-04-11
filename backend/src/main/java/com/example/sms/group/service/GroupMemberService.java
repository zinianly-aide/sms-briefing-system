package com.example.sms.group.service;

import com.example.sms.group.entity.GroupMember;

import java.util.List;

public interface GroupMemberService {
    List<GroupMember> getMembersByGroupId(Long groupId);

    void addMembers(Long groupId, List<Long> contactIds);

    void removeMember(Long groupId, Long contactId);
}
