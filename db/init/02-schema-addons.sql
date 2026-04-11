-- ============================================================
-- Schema Add-ons for SMS Briefing System
-- Execute after 01-schema.sql (tables already exist)
-- ============================================================

-- 1. Group Member (群组成员关联)
CREATE TABLE IF NOT EXISTS group_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    contact_id BIGINT NOT NULL,
    role VARCHAR(32) DEFAULT '成员',
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_group_contact (group_id, contact_id),
    INDEX idx_group_id (group_id),
    INDEX idx_contact_id (contact_id)
);

-- 2. SMS Task Recipient (发送接收人明细)
CREATE TABLE IF NOT EXISTS sms_task_recipient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    contact_id BIGINT,
    mobile VARCHAR(32) NOT NULL,
    name VARCHAR(64),
    status VARCHAR(32) DEFAULT '待发送',
    sent_at DATETIME,
    error_msg VARCHAR(256),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id),
    INDEX idx_status (status)
);

-- 3. Operation Log (操作审计日志)
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    module VARCHAR(64) NOT NULL,
    action VARCHAR(64) NOT NULL,
    operator VARCHAR(64),
    detail TEXT,
    ip VARCHAR(64),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_module (module),
    INDEX idx_operator (operator),
    INDEX idx_created_at (created_at)
);

-- 4. System Config (系统配置)
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(128) NOT NULL UNIQUE,
    config_value VARCHAR(512),
    config_desc VARCHAR(256),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. Default system configs
INSERT IGNORE INTO system_config (config_key, config_value, config_desc) VALUES
('sms_max_length', '70', '短信最大长度'),
('allow_external_number', 'false', '是否允许手动输入外部号码'),
('dashboard_page_size', '10', '首页列表默认条数'),
('mock_send_success_rate', '85', 'Mock发送成功率(百分比)'),
('number_priority', 'hr_first', '号码优先级: hr_first=HR优先, personal_first=私人优先');

-- NOTE: ALTER TABLE statements for briefing and briefing_template
-- are handled separately due to MySQL IF NOT EXISTS limitations.
-- Run the following after verifying columns don't exist:
--
-- ALTER TABLE briefing ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP;
-- ALTER TABLE briefing ADD COLUMN disaster_type VARCHAR(64) DEFAULT NULL COMMENT '灾害类别';
-- ALTER TABLE briefing ADD COLUMN disaster_level VARCHAR(32) DEFAULT NULL COMMENT '级别';
-- ALTER TABLE briefing ADD COLUMN content_part2 TEXT DEFAULT NULL COMMENT '内容二';
-- ALTER TABLE briefing ADD COLUMN remark TEXT DEFAULT NULL COMMENT '说明';
-- ALTER TABLE briefing ADD COLUMN legacy_payload TEXT DEFAULT NULL COMMENT '原始载荷';
-- ALTER TABLE briefing_template ADD COLUMN default_group_ids VARCHAR(512) DEFAULT NULL COMMENT '默认发送群组ID列表';
