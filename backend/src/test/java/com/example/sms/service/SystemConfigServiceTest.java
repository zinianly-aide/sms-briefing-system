package com.example.sms.service;

import com.example.sms.config.entity.SystemConfig;
import com.example.sms.config.mapper.SystemConfigMapper;
import com.example.sms.config.service.impl.SystemConfigServiceImpl;
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
class SystemConfigServiceTest {

    @Mock
    private SystemConfigMapper configMapper;

    private SystemConfigServiceImpl service;

    private final LocalDateTime now = LocalDateTime.now();

    private final SystemConfig sample = new SystemConfig(1L, "sms_max_length", "70", "短信最大长度", now);

    @BeforeEach
    void setUp() {
        service = new SystemConfigServiceImpl(configMapper);
    }

    @Test
    void listAll_shouldReturnAll() {
        when(configMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.listAll()).hasSize(1);
    }

    @Test
    void create_shouldReturnCreated() {
        when(configMapper.insert(any())).thenReturn(1);
        SystemConfig created = service.create(sample);
        assertThat(created.getConfigKey()).isEqualTo("sms_max_length");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(configMapper.selectById(999L)).thenReturn(null);
        SystemConfig input = new SystemConfig(999L, "missing", "val", "desc", now);
        assertThatThrownBy(() -> service.update(input)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getValue_shouldReturnValue() {
        when(configMapper.selectByKey("sms_max_length")).thenReturn(sample);
        assertThat(service.getValue("sms_max_length")).isEqualTo("70");
    }

    @Test
    void getValue_shouldReturnNullWhenMissing() {
        when(configMapper.selectByKey("missing")).thenReturn(null);
        assertThat(service.getValue("missing")).isNull();
    }
}
