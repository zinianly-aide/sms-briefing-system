package com.example.sms.smstask.service.impl;

import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.mapper.SmsTaskMapper;
import com.example.sms.smstask.service.SmsTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SmsTaskServiceImpl implements SmsTaskService {
    private final SmsTaskMapper smsTaskMapper;

    public SmsTaskServiceImpl(SmsTaskMapper smsTaskMapper) {
        this.smsTaskMapper = smsTaskMapper;
    }

    @Override
    public List<SmsTask> listAll() {
        return smsTaskMapper.selectAll();
    }

    @Override
    public SmsTask getById(Long id) {
        return smsTaskMapper.selectById(id);
    }

    @Override
    @Transactional
    public SmsTask create(SmsTask task) {
        LocalDateTime now = LocalDateTime.now();
        SmsTask created = new SmsTask(null, task.title(), task.channel(), task.plannedSendTime(), task.status(), task.recipientCount(), task.creator(), task.successRate(), now, now);
        smsTaskMapper.insert(created);
        return created;
    }

    @Override
    @Transactional
    public SmsTask update(SmsTask task) {
        SmsTask existing = getById(task.id());
        if (existing == null) {
            throw new RuntimeException("发送任务不存在");
        }
        SmsTask updated = new SmsTask(task.id(), task.title(), task.channel(), task.plannedSendTime(), task.status(), task.recipientCount(), task.creator(), task.successRate(), existing.createdAt(), LocalDateTime.now());
        smsTaskMapper.update(updated);
        return updated;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return smsTaskMapper.deleteById(id) > 0;
    }

    @Override
    public List<SmsTask> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return listAll();
        }
        return smsTaskMapper.search(keyword);
    }
}
