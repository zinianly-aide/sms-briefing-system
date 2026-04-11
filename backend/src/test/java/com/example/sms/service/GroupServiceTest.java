package com.example.sms.service;

import com.example.sms.group.entity.ContactGroup;
import com.example.sms.group.mapper.GroupMapper;
import com.example.sms.group.service.impl.GroupServiceImpl;
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
class GroupServiceTest {

    @Mock
    private GroupMapper groupMapper;

    private GroupServiceImpl service;

    private final LocalDateTime now = LocalDateTime.now();

    private final ContactGroup sample = new ContactGroup(1L, "销售群", "销售部", 10, "销售,核心", now, "启用", now, now);

    @BeforeEach
    void setUp() {
        service = new GroupServiceImpl(groupMapper);
    }

    @Test
    void listAll_shouldReturnAllGroups() {
        when(groupMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.listAll()).hasSize(1);
    }

    @Test
    void listAll_shouldReturnEmpty() {
        when(groupMapper.selectAll()).thenReturn(List.of());
        assertThat(service.listAll()).isEmpty();
    }

    @Test
    void getById_shouldReturnGroup() {
        when(groupMapper.selectById(1L)).thenReturn(sample);
        assertThat(service.getById(1L).getName()).isEqualTo("销售群");
    }

    @Test
    void getById_shouldReturnNull() {
        when(groupMapper.selectById(999L)).thenReturn(null);
        assertThat(service.getById(999L)).isNull();
    }

    @Test
    void create_shouldReturnCreatedGroup() {
        when(groupMapper.insert(any())).thenReturn(1);
        ContactGroup input = new ContactGroup(null, "研发群", "研发部", 5, "研发", null, "启用", null, null);
        ContactGroup created = service.create(input);
        assertThat(created.getName()).isEqualTo("研发群");
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    void create_withNullMemberCount_shouldDefaultToZero() {
        when(groupMapper.insert(any())).thenReturn(1);
        ContactGroup input = new ContactGroup(null, "测试群", "测试部", null, "测试", null, "启用", null, null);
        ContactGroup created = service.create(input);
        assertThat(created.getMemberCount()).isEqualTo(0);
    }

    @Test
    void update_shouldReturnUpdated() {
        when(groupMapper.selectById(1L)).thenReturn(sample);
        when(groupMapper.update(any())).thenReturn(1);
        ContactGroup input = new ContactGroup(1L, "销售群改名", "销售部", 15, "销售", now, "启用", now, now);
        ContactGroup updated = service.update(input);
        assertThat(updated.getName()).isEqualTo("销售群改名");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(groupMapper.selectById(999L)).thenReturn(null);
        ContactGroup input = new ContactGroup(999L, "不存在", "无", 0, "", null, "启用", now, now);
        assertThatThrownBy(() -> service.update(input)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("群组不存在");
    }

    @Test
    void delete_shouldReturnTrue() {
        when(groupMapper.deleteById(1L)).thenReturn(1);
        assertThat(service.delete(1L)).isTrue();
    }

    @Test
    void delete_shouldReturnFalse() {
        when(groupMapper.deleteById(999L)).thenReturn(0);
        assertThat(service.delete(999L)).isFalse();
    }

    @Test
    void search_shouldReturnResults() {
        when(groupMapper.search("销售")).thenReturn(List.of(sample));
        assertThat(service.search("销售")).hasSize(1);
    }

    @Test
    void search_withBlankKeyword_shouldReturnAll() {
        when(groupMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.search("")).hasSize(1);
    }

    @Test
    void search_noMatch_shouldReturnEmpty() {
        when(groupMapper.search("不存在")).thenReturn(List.of());
        assertThat(service.search("不存在")).isEmpty();
    }
}
