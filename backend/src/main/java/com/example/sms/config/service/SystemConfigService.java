package com.example.sms.config.service;

import com.example.sms.config.entity.SystemConfig;

import java.util.List;

public interface SystemConfigService {
    List<SystemConfig> listAll();

    SystemConfig getById(Long id);

    SystemConfig getByKey(String key);

    String getValue(String key);

    SystemConfig create(SystemConfig config);

    SystemConfig update(SystemConfig config);

    boolean delete(Long id);
}
