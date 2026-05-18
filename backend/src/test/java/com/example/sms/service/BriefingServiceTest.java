package com.example.sms.service;

import com.example.sms.briefing.entity.Briefing;
import com.example.sms.briefing.mapper.BriefingMapper;
import com.example.sms.briefing.service.impl.BriefingServiceImpl;
import com.example.sms.common.constant.DomainStatus;
import com.example.sms.group.entity.GroupMember;
import com.example.sms.group.service.GroupMemberService;
import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.entity.SmsTaskRecipient;
import com.example.sms.smstask.mapper.SmsTaskRecipientMapper;
import com.example.sms.smstask.service.SmsTaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BriefingServiceTest {

    @Mock
    private BriefingMapper briefingMapper;

    @Mock
    private SmsTaskService smsTaskService;

    @Mock
    private GroupMemberService groupMemberService;

    @Mock
    private SmsTaskRecipientMapper smsTaskRecipientMapper;

    private BriefingServiceImpl service;

    private final LocalDateTime now = LocalDateTime.now();

    private final Briefing sample = new Briefing(1L, "暴雨简讯", "请注意防汛", 1L, DomainStatus.Briefing.PENDING_REVIEW, DomainStatus.Channel.SMS, "张三", "V1.0", "1,2", now, "张三", now, null, null, null, null, null);

    @BeforeEach
    void setUp() {
        service = new BriefingServiceImpl(briefingMapper, smsTaskService, groupMemberService, smsTaskRecipientMapper, new ObjectMapper());
    }

    @Test
    void listAll_shouldReturnAllBriefings() {
        when(briefingMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.listAll()).hasSize(1);
    }

    @Test
    void listAll_shouldReturnEmpty() {
        when(briefingMapper.selectAll()).thenReturn(List.of());
        assertThat(service.listAll()).isEmpty();
    }

    @Test
    void getById_shouldReturnBriefing() {
        when(briefingMapper.selectById(1L)).thenReturn(sample);
        assertThat(service.getById(1L).getTitle()).isEqualTo("暴雨简讯");
    }

    @Test
    void getById_shouldReturnNull() {
        when(briefingMapper.selectById(999L)).thenReturn(null);
        assertThat(service.getById(999L)).isNull();
    }

    @Test
    void create_shouldReturnCreated() {
        when(briefingMapper.insert(any())).thenReturn(1);
        Briefing input = new Briefing(null, "新简讯", "内容", null, DomainStatus.Briefing.DRAFT, DomainStatus.Channel.SMS, "李四", "V1.0", "", null, "李四", null, null, null, null, null, null);
        Briefing created = service.create(input);
        assertThat(created.getTitle()).isEqualTo("新简讯");
        assertThat(created.getUpdatedAt()).isNotNull();
    }

    @Test
    void create_shouldCreateRecurringTaskForRecurringSchedule() {
        when(briefingMapper.insert(any())).thenReturn(1);
        when(groupMemberService.getMembersByGroupId(10L)).thenReturn(List.of(
            buildMember(10L, 100L, "张三", "13800000000"),
            buildMember(10L, 101L, "李四", "13900000000")
        ));
        when(groupMemberService.getMembersByGroupId(11L)).thenReturn(List.of(
            buildMember(11L, 100L, "张三", "13800000000")
        ));
        when(smsTaskService.create(any())).thenAnswer(invocation -> {
            SmsTask task = invocation.getArgument(0);
            task.setId(99L);
            return task;
        });

        Briefing input = new Briefing(
            null,
            "循环通知",
            "内容",
            1L,
            DomainStatus.Briefing.PENDING_SEND,
            DomainStatus.Channel.SMS,
            "李四",
            "V1.0",
            "10,11",
            null,
            "李四",
            null,
            null,
            null,
            null,
            null,
            "{\"scheduleType\":\"recurring\",\"scheduledTime\":\"2030-01-01T09:00:00\",\"recurrenceInterval\":2,\"recurrenceUnit\":\"day\",\"recurrenceEndTime\":\"2030-01-15T09:00:00\",\"groupIds\":[10,11]}"
        );

        service.create(input);

        verify(smsTaskService).create(argThat(task ->
            "循环通知".equals(task.getTitle())
                && "recurring".equals(task.getScheduleType())
                && Integer.valueOf(2).equals(task.getRecurrenceInterval())
                && "day".equals(task.getRecurrenceUnit())
                && task.getPlannedSendTime() != null
                && Integer.valueOf(2).equals(task.getRecipientCount())
        ));
        verify(smsTaskRecipientMapper, times(2)).insert(any(SmsTaskRecipient.class));
    }

    @Test
    void update_shouldReturnUpdated() {
        when(briefingMapper.selectById(1L)).thenReturn(sample);
        when(briefingMapper.update(any())).thenReturn(1);
        Briefing input = new Briefing(1L, "暴雨简讯改", "请注意防汛！", 1L, DomainStatus.Briefing.PENDING_SEND, DomainStatus.Channel.SMS_WECOM, "张三", "V1.1", "1,2,3", now, "张三", now, null, null, null, null, null);
        assertThat(service.update(input).getContent()).isEqualTo("请注意防汛！");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(briefingMapper.selectById(999L)).thenReturn(null);
        Briefing input = new Briefing(999L, "不存在", "无", null, DomainStatus.Briefing.DRAFT, DomainStatus.Channel.SMS, "无", "V1.0", "", now, "无", now, null, null, null, null, null);
        assertThatThrownBy(() -> service.update(input)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("简讯不存在");
    }

    @Test
    void delete_shouldReturnTrue() {
        when(briefingMapper.deleteById(1L)).thenReturn(1);
        assertThat(service.delete(1L)).isTrue();
    }

    @Test
    void delete_shouldReturnFalse() {
        when(briefingMapper.deleteById(999L)).thenReturn(0);
        assertThat(service.delete(999L)).isFalse();
    }

    @Test
    void search_shouldReturnResults() {
        when(briefingMapper.search("暴雨")).thenReturn(List.of(sample));
        assertThat(service.search("暴雨")).hasSize(1);
    }

    @Test
    void search_withBlankKeyword_shouldReturnAll() {
        when(briefingMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.search("")).hasSize(1);
    }

    @Test
    void search_noMatch_shouldReturnEmpty() {
        when(briefingMapper.search("不存在")).thenReturn(List.of());
        assertThat(service.search("不存在")).isEmpty();
    }

    private GroupMember buildMember(Long groupId, Long contactId, String name, String mobile) {
        GroupMember member = new GroupMember(null, groupId, contactId, "成员", now);
        member.setContactName(name);
        member.setContactMobile(mobile);
        return member;
    }
}
