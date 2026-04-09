CREATE TABLE IF NOT EXISTS contact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    mobile VARCHAR(32) NOT NULL,
    department VARCHAR(64),
    title VARCHAR(64),
    status VARCHAR(32) DEFAULT 'active'
);

CREATE TABLE IF NOT EXISTS contact_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    owner_dept VARCHAR(64),
    member_count INT DEFAULT 0,
    status VARCHAR(32) DEFAULT '启用'
);

CREATE TABLE IF NOT EXISTS briefing_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    category VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(32) DEFAULT '启用中',
    owner VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS briefing (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(128) NOT NULL,
    content TEXT NOT NULL,
    template_id BIGINT,
    status VARCHAR(32) DEFAULT '待审核',
    created_by VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS sms_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_name VARCHAR(128) NOT NULL,
    channel VARCHAR(32) NOT NULL,
    planned_send_time DATETIME,
    status VARCHAR(32) DEFAULT '待发送',
    recipient_count INT DEFAULT 0,
    briefing_id BIGINT
);
