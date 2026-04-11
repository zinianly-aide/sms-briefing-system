package com.example.sms.group.service.impl;

import com.example.sms.group.entity.GroupMember;
import com.example.sms.group.mapper.GroupMemberMapper;
import com.example.sms.group.service.GroupMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberMapper groupMemberMapper;

    public GroupMemberServiceImpl(GroupMemberMapper groupMemberMapper) {
        this.groupMemberMapper = groupMemberMapper;
    }

    @Override
    public List<GroupMember> getMembersByGroupId(Long groupId) {
        return groupMemberMapper.selectByGroupId(groupId);
    }

    @Override
    @Transactional
    public void addMembers(Long groupId, List<Long> contactIds) {
        for (Long contactId : contactIds) {
            GroupMember member = new GroupMember(null, groupId, contactId, "成员", LocalDateTime.now());
            groupMemberMapper.insert(member);
        }
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long contactId) {
        groupMemberMapper.delete(groupId, contactId);
    }
}
