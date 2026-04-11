package com.example.sms.contact.service;

public interface HrSyncService {
    /**
     * 同步HR数据，返回 {synced: 同步数, skipped: 跳过数}
     */
    java.util.Map<String, Object> sync();
}
