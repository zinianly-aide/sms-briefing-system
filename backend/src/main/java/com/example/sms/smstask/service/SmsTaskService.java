package com.example.sms.smstask.service;

import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.entity.SmsTaskRecipient;

import java.util.List;

public interface SmsTaskService {
    List<SmsTask> listAll();

    SmsTask getById(Long id);

    SmsTask create(SmsTask task);

    SmsTask update(SmsTask task);

    boolean delete(Long id);

    List<SmsTask> search(String keyword);

    /**
     * 执行发送任务
     */
    void executeTask(Long taskId);

    /**
     * 取消任务
     */
    void cancelTask(Long taskId, String reason);

    /**
     * 扫描并执行到期的预约任务
     */
    void scanAndExecuteScheduledTasks();

    /**
     * 获取任务接收人明细
     */
    List<SmsTaskRecipient> getRecipients(Long taskId);
}
