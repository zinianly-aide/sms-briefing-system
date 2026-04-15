package com.example.sms.service;

import com.example.sms.common.constant.DomainStatus;
import com.example.sms.template.entity.Template;
import com.example.sms.template.mapper.TemplateMapper;
import com.example.sms.template.service.impl.TemplateServiceImpl;
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
class TemplateServiceTest {

    @Mock
    private TemplateMapper templateMapper;

    private TemplateServiceImpl service;

    private final LocalDateTime now = LocalDateTime.now();

    private final Template sample = new Template(1L, "预警模板", "预警", "请注意安全", DomainStatus.Template.ACTIVE, "运营", null, now);

    @BeforeEach
    void setUp() {
        service = new TemplateServiceImpl(templateMapper);
    }

    @Test
    void listAll_shouldReturnAllTemplates() {
        when(templateMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.listAll()).hasSize(1);
    }

    @Test
    void listAll_shouldReturnEmpty() {
        when(templateMapper.selectAll()).thenReturn(List.of());
        assertThat(service.listAll()).isEmpty();
    }

    @Test
    void getById_shouldReturnTemplate() {
        when(templateMapper.selectById(1L)).thenReturn(sample);
        assertThat(service.getById(1L).getName()).isEqualTo("预警模板");
    }

    @Test
    void getById_shouldReturnNull() {
        when(templateMapper.selectById(999L)).thenReturn(null);
        assertThat(service.getById(999L)).isNull();
    }

    @Test
    void create_shouldReturnCreated() {
        when(templateMapper.insert(any())).thenReturn(1);
        Template input = new Template(null, "通知模板", "通知", "内容", DomainStatus.Template.DRAFT, "管理员", null, null);
        Template created = service.create(input);
        assertThat(created.getName()).isEqualTo("通知模板");
        assertThat(created.getUpdatedAt()).isNotNull();
    }

    @Test
    void update_shouldReturnUpdated() {
        when(templateMapper.selectById(1L)).thenReturn(sample);
        when(templateMapper.update(any())).thenReturn(1);
        Template input = new Template(1L, "预警模板改", "预警", "新内容", DomainStatus.Template.ACTIVE, "运营", null, now);
        assertThat(service.update(input).getContent()).isEqualTo("新内容");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(templateMapper.selectById(999L)).thenReturn(null);
        Template input = new Template(999L, "不存在", "无", "无", DomainStatus.Template.DRAFT, "无", null, now);
        assertThatThrownBy(() -> service.update(input)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("模板不存在");
    }

    @Test
    void delete_shouldReturnTrue() {
        when(templateMapper.deleteById(1L)).thenReturn(1);
        assertThat(service.delete(1L)).isTrue();
    }

    @Test
    void delete_shouldReturnFalse() {
        when(templateMapper.deleteById(999L)).thenReturn(0);
        assertThat(service.delete(999L)).isFalse();
    }

    @Test
    void search_shouldReturnResults() {
        when(templateMapper.search("预警")).thenReturn(List.of(sample));
        assertThat(service.search("预警")).hasSize(1);
    }

    @Test
    void search_withBlankKeyword_shouldReturnAll() {
        when(templateMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.search("")).hasSize(1);
    }

    @Test
    void search_noMatch_shouldReturnEmpty() {
        when(templateMapper.search("不存在")).thenReturn(List.of());
        assertThat(service.search("不存在")).isEmpty();
    }
}
