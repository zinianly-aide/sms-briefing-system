package com.example.sms.smstask.service.impl;
import com.example.sms.smstask.entity.SmsTask; import com.example.sms.smstask.service.SmsTaskService; import org.springframework.stereotype.Service; import java.util.List;
@Service public class SmsTaskServiceImpl implements SmsTaskService { public List<SmsTask> list(){ return List.of(new SmsTask(301L,"台风预警任务","PENDING")); } }
