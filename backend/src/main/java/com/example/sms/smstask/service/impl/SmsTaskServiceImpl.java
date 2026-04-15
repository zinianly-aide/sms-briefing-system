package com.example.sms.smstask.service.impl;

import com.example.sms.common.constant.DomainStatus;
import com.example.sms.common.dto.PageResult;
import com.example.sms.common.exception.BusinessException;
import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.entity.SmsTaskRecipient;
import com.example.sms.smstask.mapper.SmsTaskMapper;
import com.example.sms.smstask.mapper.SmsTaskRecipientMapper;
import com.example.sms.smstask.service.SmsGatewayService;
import com.example.sms.smstask.service.SmsTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmsTaskServiceImpl implements SmsTaskService {
    private static final Logger log = LoggerFactory.getLogger(SmsTaskServiceImpl.class);

    private final SmsTaskMapper smsTaskMapper;
    private final SmsTaskRecipientMapper recipientMapper;
    private final SmsGatewayService smsGatewayService;

    public SmsTaskServiceImpl(SmsTaskMapper smsTaskMapper,
                              SmsTaskRecipientMapper recipientMapper,
                              SmsGatewayService smsGatewayService) {
        this.smsTaskMapper = smsTaskMapper;
        this.recipientMapper = recipientMapper;
        this.smsGatewayService = smsGatewayService;
    }

    @Override
    public PageResult<SmsTask> listPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<SmsTask> list = smsTaskMapper.selectPage(pageSize, offset);
        int total = smsTaskMapper.count();
        return PageResult.of(list, total, page, pageSize);
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
        SmsTask created = new SmsTask(
            null,
            task.getTitle(),
            StringUtils.hasText(task.getChannel()) ? task.getChannel() : DomainStatus.Channel.SMS,
            task.getPlannedSendTime(),
            StringUtils.hasText(task.getStatus()) ? task.getStatus() : DomainStatus.Task.DRAFT,
            task.getRecipientCount(),
            task.getCreator(),
            task.getSuccessRate(),
            now,
            now
        );
        smsTaskMapper.insert(created);
        return created;
    }

    @Override
    @Transactional
    public SmsTask update(SmsTask task) {
        SmsTask existing = getById(task.getId());
        if (existing == null) {
            throw new BusinessException(404, "发送任务不存在");
        }
        SmsTask updated = new SmsTask(task.getId(), task.getTitle(), task.getChannel(), task.getPlannedSendTime(), task.getStatus(), task.getRecipientCount(), task.getCreator(), task.getSuccessRate(), existing.getCreatedAt(), LocalDateTime.now());
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

    @Override
    public PageResult<SmsTask> searchPaged(String keyword, int page, int pageSize) {
        if (!StringUtils.hasText(keyword)) {
            return listPaged(page, pageSize);
        }
        int offset = (page - 1) * pageSize;
        List<SmsTask> list = smsTaskMapper.searchPage(keyword, pageSize, offset);
        int total = smsTaskMapper.countByKeyword(keyword);
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    @Transactional
    public void executeTask(Long taskId) {
        SmsTask task = getById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        if (!DomainStatus.Task.PENDING.equals(task.getStatus()) && !DomainStatus.Task.DRAFT.equals(task.getStatus())) {
            throw new BusinessException(400, "任务状态不允许发送: " + task.getStatus());
        }

        // Update task status to sending
        updateTaskStatus(task, DomainStatus.Task.SENDING);

        List<SmsTaskRecipient> recipients = recipientMapper.selectByTaskId(taskId);
        if (recipients.isEmpty()) {
            updateTaskStatus(task, DomainStatus.Task.FAILED);
            return;
        }

        List<String> mobiles = recipients.stream().map(SmsTaskRecipient::getMobile).collect(Collectors.toList());

        // Call gateway
        List<SmsGatewayService.SendResult> results = smsGatewayService.send(mobiles, task.getTitle());

        int successCount = 0;
        for (int i = 0; i < results.size() && i < recipients.size(); i++) {
            SmsGatewayService.SendResult result = results.get(i);
            SmsTaskRecipient recipient = recipients.get(i);
            recipient.setStatus(result.success() ? DomainStatus.Recipient.SUCCESS : DomainStatus.Recipient.FAILED);
            recipient.setSentAt(result.success() ? LocalDateTime.now() : null);
            recipient.setErrorMsg(result.errorMsg());
            recipientMapper.updateStatus(recipient);
            if (result.success()) successCount++;
        }

        int total = recipients.size();
        String finalStatus;
        String successRate;

        if (successCount == total) {
            finalStatus = DomainStatus.Task.COMPLETED;
            successRate = "100%";
        } else if (successCount == 0) {
            finalStatus = DomainStatus.Task.FAILED;
            successRate = "0%";
        } else {
            finalStatus = DomainStatus.Task.PARTIAL_SUCCESS;
            successRate = successCount + "/" + total;
        }

        SmsTask updated = new SmsTask(task.getId(), task.getTitle(), task.getChannel(), task.getPlannedSendTime(),
            finalStatus, total, task.getCreator(), successRate, task.getCreatedAt(), LocalDateTime.now());
        smsTaskMapper.update(updated);
        log.info("Task {} executed: {} sent, status={}", taskId, successCount, finalStatus);
    }

    @Override
    @Transactional
    public void cancelTask(Long taskId, String reason) {
        SmsTask task = getById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        if (!DomainStatus.Task.PENDING.equals(task.getStatus()) && !DomainStatus.Task.DRAFT.equals(task.getStatus())) {
            throw new BusinessException(400, "当前状态不允许取消: " + task.getStatus());
        }
        updateTaskStatus(task, DomainStatus.Task.CANCELLED);
    }

    @Override
    @Transactional
    public void scanAndExecuteScheduledTasks() {
        List<SmsTask> all = listAll();
        LocalDateTime now = LocalDateTime.now();
        for (SmsTask task : all) {
            if (DomainStatus.Task.PENDING.equals(task.getStatus()) && task.getPlannedSendTime() != null && task.getPlannedSendTime().isBefore(now)) {
                log.info("Executing scheduled task: {} (planned: {})", task.getId(), task.getPlannedSendTime());
                try {
                    executeTask(task.getId());
                } catch (Exception e) {
                    log.error("Failed to execute scheduled task {}: {}", task.getId(), e.getMessage());
                }
            }
        }
    }

    @Override
    public List<SmsTaskRecipient> getRecipients(Long taskId) {
        return recipientMapper.selectByTaskId(taskId);
    }

    private void updateTaskStatus(SmsTask task, String status) {
        SmsTask updated = new SmsTask(task.getId(), task.getTitle(), task.getChannel(), task.getPlannedSendTime(),
            status, task.getRecipientCount(), task.getCreator(), task.getSuccessRate(), task.getCreatedAt(), LocalDateTime.now());
        smsTaskMapper.update(updated);
    }
}
