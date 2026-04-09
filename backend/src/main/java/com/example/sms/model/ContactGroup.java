package com.example.sms.model;

import java.util.List;

public record ContactGroup(Long id, String name, String ownerDept, int memberCount, String lastSyncTime,
                           List<String> tags) {
}
