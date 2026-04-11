package com.example.sms.audit.service;

import com.example.sms.audit.entity.OperationLog;

import java.util.List;

public interface AuditLogService {
    List<OperationLog> listAll();
    List<OperationLog> getByModule(String module);
    List<OperationLog> getByOperator(String operator);
}
