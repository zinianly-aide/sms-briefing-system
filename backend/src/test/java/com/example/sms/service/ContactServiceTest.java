package com.example.sms.service;

import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.mapper.ContactMapper;
import com.example.sms.contact.service.impl.ContactServiceImpl;
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
class ContactServiceTest {

    @Mock
    private ContactMapper contactMapper;

    private ContactServiceImpl service;

    private final LocalDateTime now = LocalDateTime.now();

    private final ContactEntity sample = new ContactEntity(1L, "张三", "13800000000", "销售", "经理", "active", now, now);

    @BeforeEach
    void setUp() {
        service = new ContactServiceImpl();
        // inject via reflection since @Autowired is used
        try {
            var field = ContactServiceImpl.class.getDeclaredField("contactMapper");
            field.setAccessible(true);
            field.set(service, contactMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listAll_shouldReturnAllContacts() {
        when(contactMapper.selectAll()).thenReturn(List.of(sample));
        List<ContactEntity> result = service.listAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("张三");
    }

    @Test
    void listAll_shouldReturnEmptyList() {
        when(contactMapper.selectAll()).thenReturn(List.of());
        assertThat(service.listAll()).isEmpty();
    }

    @Test
    void getById_shouldReturnContact() {
        when(contactMapper.selectById(1L)).thenReturn(sample);
        ContactEntity result = service.getById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getMobile()).isEqualTo("13800000000");
    }

    @Test
    void getById_shouldReturnNullWhenNotFound() {
        when(contactMapper.selectById(999L)).thenReturn(null);
        assertThat(service.getById(999L)).isNull();
    }

    @Test
    void getByMobile_shouldReturnContact() {
        when(contactMapper.selectByMobile("13800000000")).thenReturn(sample);
        assertThat(service.getByMobile("13800000000")).isNotNull();
    }

    @Test
    void create_shouldReturnCreatedContact() {
        when(contactMapper.insert(any())).thenReturn(1);
        ContactEntity input = new ContactEntity(null, "李四", "13900000000", "研发", "工程师", "active", null, null);
        ContactEntity created = service.create(input);
        assertThat(created.getName()).isEqualTo("李四");
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    void create_shouldThrowWhenInsertFails() {
        when(contactMapper.insert(any())).thenReturn(0);
        ContactEntity input = new ContactEntity(null, "王五", "15000000000", "人事", "专员", "active", null, null);
        assertThatThrownBy(() -> service.create(input)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("创建联系人失败");
    }

    @Test
    void update_shouldReturnUpdatedContact() {
        when(contactMapper.selectById(1L)).thenReturn(sample);
        when(contactMapper.update(any())).thenReturn(1);
        ContactEntity input = new ContactEntity(1L, "张三改", "13800000000", "销售", "总监", "active", now, now);
        ContactEntity updated = service.update(input);
        assertThat(updated.getName()).isEqualTo("张三改");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(contactMapper.selectById(999L)).thenReturn(null);
        ContactEntity input = new ContactEntity(999L, "不存在", "000", "无", "无", "active", now, now);
        assertThatThrownBy(() -> service.update(input)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("联系人不存在");
    }

    @Test
    void delete_shouldReturnTrue() {
        when(contactMapper.selectById(1L)).thenReturn(sample);
        when(contactMapper.deleteById(1L)).thenReturn(1);
        assertThat(service.delete(1L)).isTrue();
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(contactMapper.selectById(999L)).thenReturn(null);
        assertThatThrownBy(() -> service.delete(999L)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("联系人不存在");
    }

    @Test
    void getByDepartment_shouldReturnFilteredContacts() {
        when(contactMapper.selectByDepartment("销售")).thenReturn(List.of(sample));
        List<ContactEntity> result = service.getByDepartment("销售");
        assertThat(result).hasSize(1);
    }

    @Test
    void getByStatus_shouldReturnFilteredContacts() {
        when(contactMapper.selectByStatus("active")).thenReturn(List.of(sample));
        List<ContactEntity> result = service.getByStatus("active");
        assertThat(result).hasSize(1);
    }

    @Test
    void search_shouldReturnResults() {
        when(contactMapper.searchByKeyword("张")).thenReturn(List.of(sample));
        List<ContactEntity> result = service.search("张");
        assertThat(result).hasSize(1);
    }

    @Test
    void search_withBlankKeyword_shouldReturnAll() {
        when(contactMapper.selectAll()).thenReturn(List.of(sample));
        assertThat(service.search("")).hasSize(1);
        assertThat(service.search("   ")).hasSize(1);
    }

    @Test
    void search_noMatch_shouldReturnEmpty() {
        when(contactMapper.searchByKeyword("不存在")).thenReturn(List.of());
        assertThat(service.search("不存在")).isEmpty();
    }
}
