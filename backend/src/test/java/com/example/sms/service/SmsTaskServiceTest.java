package com.example.sms.service;

import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.mapper.SmsTaskMapper;
import com.example.sms.smstask.mapper.SmsTaskRecipientMapper;
import com.example.sms.smstask.service.SmsGatewayService;
import com.example.sms.smstask.service.impl.SmsTaskServiceImpl;
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
class SmsTaskServiceTest {

    @Mock
    private SmsTaskMapper smsTaskMapper;

    @Mock
    private SmsTaskRecipientMapper recipientMapper;

    @Mock
    private SmsGatewayService smsGatewayService;

    private SmsTaskServiceImpl service;

    private final LocalDateTime now = LocalDateTime.now();

    private final SmsTask sample = new SmsTask(1L, "暴雨提醒", "短信", now, "待发送", 10, "张三", "—", now, now);

    @BeforeEach
    void setUp() {
        service = new SmsTaskServiceImpl(smsTaskMapper, recipientMapper, smsGatewayService);
    }

    @Test
    void listAll_shouldReturnAllTasks() {
        when(smsTaskMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.listAll()).hasSize(1);
    }

    @Test
    void listAll_shouldReturnEmpty() {
        when(smsTaskMapper.selectAll()).thenReturn(List.of());
        assertThat(service.listAll()).isEmpty();
    }

    @Test
    void getById_shouldReturnTask() {
        when(smsTaskMapper.selectById(1L)).thenReturn(sample);
        assertThat(service.getById(1L).getTitle()).isEqualTo("暴雨提醒");
    }

    @Test
    void getById_shouldReturnNull() {
        when(smsTaskMapper.selectById(999L)).thenReturn(null);
        assertThat(service.getById(999L)).isNull();
    }

    @Test
    void create_shouldReturnCreated() {
        when(smsTaskMapper.insert(any())).thenReturn(1);
        SmsTask input = new SmsTask(null, "新任务", "短信", null, "草稿", 0, "李四", null, null, null);
        SmsTask created = service.create(input);
        assertThat(created.getTitle()).isEqualTo("新任务");
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    void update_shouldReturnUpdated() {
        when(smsTaskMapper.selectById(1L)).thenReturn(sample);
        when(smsTaskMapper.update(any())).thenReturn(1);
        SmsTask input = new SmsTask(1L, "暴雨提醒改", "短信+企微", now, "已完成", 20, "张三", "100%", now, now);
        assertThat(service.update(input).getTitle()).isEqualTo("暴雨提醒改");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(smsTaskMapper.selectById(999L)).thenReturn(null);
        SmsTask input = new SmsTask(999L, "不存在", "短信", null, "草稿", 0, "无", null, now, now);
        assertThatThrownBy(() -> service.update(input)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("发送任务不存在");
    }

    @Test
    void delete_shouldReturnTrue() {
        when(smsTaskMapper.deleteById(1L)).thenReturn(1);
        assertThat(service.delete(1L)).isTrue();
    }

    @Test
    void delete_shouldReturnFalse() {
        when(smsTaskMapper.deleteById(999L)).thenReturn(0);
        assertThat(service.delete(999L)).isFalse();
    }

    @Test
    void search_shouldReturnResults() {
        when(smsTaskMapper.search("暴雨")).thenReturn(List.of(sample));
        assertThat(service.search("暴雨")).hasSize(1);
    }

    @Test
    void search_withBlankKeyword_shouldReturnAll() {
        when(smsTaskMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.search("")).hasSize(1);
    }

    @Test
    void search_noMatch_shouldReturnEmpty() {
        when(smsTaskMapper.search("不存在")).thenReturn(List.of());
        assertThat(service.search("不存在")).isEmpty();
    }
}
