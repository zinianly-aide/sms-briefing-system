package com.example.sms.service;

import com.example.sms.group.entity.GroupMember;
import com.example.sms.group.mapper.GroupMemberMapper;
import com.example.sms.group.service.impl.GroupMemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMemberServiceTest {

    @Mock
    private GroupMemberMapper groupMemberMapper;

    private GroupMemberServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GroupMemberServiceImpl(groupMemberMapper);
    }

    @Test
    void getMembersByGroupId_shouldReturnMembers() {
        GroupMember member = new GroupMember(1L, 1L, 100L, "成员", LocalDateTime.now());
        member.setContactName("张三");
        when(groupMemberMapper.selectByGroupId(1L)).thenReturn(List.of(member));
        assertThat(service.getMembersByGroupId(1L)).hasSize(1);
    }

    @Test
    void addMembers_shouldInsertAll() {
        when(groupMemberMapper.insert(any())).thenReturn(1);
        service.addMembers(1L, List.of(100L, 101L));
        verify(groupMemberMapper, times(2)).insert(any());
    }

    @Test
    void removeMember_shouldDelete() {
        when(groupMemberMapper.delete(1L, 100L)).thenReturn(1);
        service.removeMember(1L, 100L);
        verify(groupMemberMapper).delete(1L, 100L);
    }
}
