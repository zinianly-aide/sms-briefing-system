package com.example.sms.contact.service.impl;

import com.example.sms.common.constant.DomainStatus;
import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.mapper.ContactMapper;
import com.example.sms.contact.service.HrSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class HrSyncServiceImpl implements HrSyncService {
    private static final Logger log = LoggerFactory.getLogger(HrSyncServiceImpl.class);

    private final ContactMapper contactMapper;

    private static final String[] FIRST_NAMES = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴", "徐", "孙", "马", "朱", "胡", "郭", "何", "林", "罗", "高"};
    private static final String[] LAST_NAMES = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "洋", "勇", "艳", "杰", "涛", "明", "超", "秀英", "华", "亮", "桂英", "文"};
    private static final String[] DEPARTMENTS = {"技术研发部", "市场营销部", "人力资源部", "财务部", "运营管理部", "安全管理部", "综合办公室", "客户服务部"};
    private static final String[] TITLES = {"总经理", "副总经理", "部门经理", "主管", "专员", "助理", "工程师", "高级工程师", "分析师", "实习生"};

    public HrSyncServiceImpl(ContactMapper contactMapper) {
        this.contactMapper = contactMapper;
    }

    @Override
    @Transactional
    public Map<String, Object> sync() {
        int synced = 0;
        int skipped = 0;
        int count = ThreadLocalRandom.current().nextInt(5, 15);

        for (int i = 0; i < count; i++) {
            String firstName = FIRST_NAMES[ThreadLocalRandom.current().nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[ThreadLocalRandom.current().nextInt(LAST_NAMES.length)];
            String name = firstName + lastName;
            String mobile = "1" + String.valueOf(30 + ThreadLocalRandom.current().nextInt(9)) +
                String.format("%09d", ThreadLocalRandom.current().nextInt(1000000000));
            String department = DEPARTMENTS[ThreadLocalRandom.current().nextInt(DEPARTMENTS.length)];
            String title = TITLES[ThreadLocalRandom.current().nextInt(TITLES.length)];

            ContactEntity existing = contactMapper.selectByMobile(mobile);
            if (existing != null) {
                skipped++;
                continue;
            }

            ContactEntity contact = new ContactEntity(null, name, mobile, department, title, DomainStatus.Contact.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now());
            contactMapper.insert(contact);
            synced++;
        }

        log.info("HR sync completed: synced={}, skipped={}", synced, skipped);
        Map<String, Object> result = new HashMap<>();
        result.put("synced", synced);
        result.put("skipped", skipped);
        return result;
    }
}
