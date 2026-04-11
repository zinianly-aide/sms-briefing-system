package com.example.sms.smstask.scheduler;

import com.example.sms.smstask.service.SmsTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TaskScheduler {
    private static final Logger log = LoggerFactory.getLogger(TaskScheduler.class);

    private final SmsTaskService smsTaskService;

    public TaskScheduler(SmsTaskService smsTaskService) {
        this.smsTaskService = smsTaskService;
    }

    @Scheduled(fixedRate = 60000)
    public void scanScheduledTasks() {
        log.debug("Scanning for scheduled tasks...");
        smsTaskService.scanAndExecuteScheduledTasks();
    }
}
