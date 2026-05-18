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
import java.util.List;
import java.util.Locale;
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
            now,
            normalizeScheduleType(task.getScheduleType()),
            task.getRecurrenceInterval(),
            task.getRecurrenceUnit(),
            task.getRecurrenceEndTime(),
            task.getRecurrenceCount() == null ? 0 : task.getRecurrenceCount(),
            task.getRecurrenceMaxCount()
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
        SmsTask updated = copyTask(existing);
        updated.setTitle(task.getTitle());
        updated.setChannel(task.getChannel());
        updated.setPlannedSendTime(task.getPlannedSendTime());
        updated.setStatus(task.getStatus());
        updated.setRecipientCount(task.getRecipientCount());
        updated.setCreator(task.getCreator());
        updated.setSuccessRate(task.getSuccessRate());
        updated.setUpdatedAt(LocalDateTime.now());
        updated.setScheduleType(normalizeScheduleType(task.getScheduleType()));
        updated.setRecurrenceInterval(task.getRecurrenceInterval());
        updated.setRecurrenceUnit(task.getRecurrenceUnit());
        updated.setRecurrenceEndTime(task.getRecurrenceEndTime());
        updated.setRecurrenceCount(task.getRecurrenceCount() == null ? existing.getRecurrenceCount() : task.getRecurrenceCount());
        updated.setRecurrenceMaxCount(task.getRecurrenceMaxCount());
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

        int nextRecurrenceCount = (task.getRecurrenceCount() == null ? 0 : task.getRecurrenceCount()) + 1;
        LocalDateTime nextPlannedTime = calculateNextPlannedTime(task);
        boolean shouldContinueRecurring = isRecurringTask(task) && canContinueRecurring(task, nextRecurrenceCount, nextPlannedTime);

        SmsTask updated = copyTask(task);
        updated.setRecipientCount(total);
        updated.setSuccessRate(successRate);
        updated.setUpdatedAt(LocalDateTime.now());
        updated.setRecurrenceCount(nextRecurrenceCount);
        if (shouldContinueRecurring) {
            updated.setStatus(DomainStatus.Task.PENDING);
            updated.setPlannedSendTime(nextPlannedTime);
        } else {
            updated.setStatus(finalStatus);
        }
        smsTaskMapper.update(updated);
        log.info("Task {} executed: {} sent, status={}, recurring={}", taskId, successCount, updated.getStatus(), shouldContinueRecurring);
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
            if (DomainStatus.Task.PENDING.equals(task.getStatus()) && task.getPlannedSendTime() != null && !task.getPlannedSendTime().isAfter(now)) {
                log.info("Executing scheduled task: {} (planned: {}, scheduleType={})", task.getId(), task.getPlannedSendTime(), task.getScheduleType());
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
        SmsTask updated = copyTask(task);
        updated.setStatus(status);
        updated.setUpdatedAt(LocalDateTime.now());
        smsTaskMapper.update(updated);
    }

    private SmsTask copyTask(SmsTask task) {
        return new SmsTask(
            task.getId(),
            task.getTitle(),
            task.getChannel(),
            task.getPlannedSendTime(),
            task.getStatus(),
            task.getRecipientCount(),
            task.getCreator(),
            task.getSuccessRate(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            normalizeScheduleType(task.getScheduleType()),
            task.getRecurrenceInterval(),
            task.getRecurrenceUnit(),
            task.getRecurrenceEndTime(),
            task.getRecurrenceCount() == null ? 0 : task.getRecurrenceCount(),
            task.getRecurrenceMaxCount()
        );
    }

    private boolean isRecurringTask(SmsTask task) {
        return "recurring".equalsIgnoreCase(normalizeScheduleType(task.getScheduleType()));
    }

    private String normalizeScheduleType(String scheduleType) {
        return StringUtils.hasText(scheduleType) ? scheduleType.toLowerCase(Locale.ROOT) : "immediate";
    }

    private LocalDateTime calculateNextPlannedTime(SmsTask task) {
        if (!isRecurringTask(task) || task.getPlannedSendTime() == null) {
            return null;
        }
        int interval = task.getRecurrenceInterval() == null || task.getRecurrenceInterval() <= 0 ? 1 : task.getRecurrenceInterval();
        String unit = StringUtils.hasText(task.getRecurrenceUnit()) ? task.getRecurrenceUnit().toLowerCase(Locale.ROOT) : "day";
        return switch (unit) {
            case "hour" -> task.getPlannedSendTime().plusHours(interval);
            case "week" -> task.getPlannedSendTime().plusWeeks(interval);
            case "month" -> task.getPlannedSendTime().plusMonths(interval);
            default -> task.getPlannedSendTime().plusDays(interval);
        };
    }

    private boolean canContinueRecurring(SmsTask task, int nextRecurrenceCount, LocalDateTime nextPlannedTime) {
        if (nextPlannedTime == null) {
            return false;
        }
        if (task.getRecurrenceMaxCount() != null && nextRecurrenceCount >= task.getRecurrenceMaxCount()) {
            return false;
        }
        return task.getRecurrenceEndTime() == null || !nextPlannedTime.isAfter(task.getRecurrenceEndTime());
    }
}
