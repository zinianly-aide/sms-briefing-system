package com.example.sms.service;

import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.mapper.ContactMapper;
import com.example.sms.contact.service.impl.HrSyncServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HrSyncServiceTest {

    @Mock
    private ContactMapper contactMapper;

    private HrSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new HrSyncServiceImpl(contactMapper);
    }

    @Test
    void sync_shouldInsertNewContacts() {
        when(contactMapper.selectByMobile(any())).thenReturn(null);
        when(contactMapper.insert(any())).thenReturn(1);

        Map<String, Object> result = service.sync();

        assertThat(result).containsKey("synced");
        assertThat(result).containsKey("skipped");
        assertThat(((Number) result.get("synced")).intValue()).isGreaterThan(0);
    }

    @Test
    void sync_shouldSkipExistingContacts() {
        ContactEntity existing = new ContactEntity();
        when(contactMapper.selectByMobile(any())).thenReturn(existing);

        Map<String, Object> result = service.sync();

        assertThat(((Number) result.get("skipped")).intValue()).isGreaterThan(0);
        verify(contactMapper, never()).insert(any());
    }
}
