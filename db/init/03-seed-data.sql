-- ============================================================
-- User table + Seed data
-- ============================================================

CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    display_name VARCHAR(128),
    role VARCHAR(32) DEFAULT 'user',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
);

-- Default admin: password is admin123 (BCrypt encoded)
INSERT IGNORE INTO user (username, password, display_name, role) VALUES
('admin', '$2a$10$YuoxnSc8M/EbRa/eBF0A5.Cux7jy8JKy2IYFKUaSB408HBesv9.TO', '系统管理员', 'admin');

-- Default demo user: password is Demo1234
INSERT IGNORE INTO user (username, password, display_name, role) VALUES
('demo', '$2a$10$jv07qZGs6KFI/jY0ZgK8jehO/xbX7oqtoV9WxPYsPAfstzKrdND8m', '演示用户', 'user');

-- Sample templates
INSERT IGNORE INTO briefing_template (name, category, content, status, owner) VALUES
('暴雨预警模板', '预警', '【暴雨预警】请注意安全，避免外出，做好防汛准备。', 'active', '系统'),
('台风预警模板', '预警', '【台风预警】台风即将来临，请做好防护准备，关好门窗。', 'active', '系统'),
('会议通知模板', '通知', '【会议通知】请于今日下午2点参加紧急会议，地点：会议室A。', 'active', '运营'),
('节假日祝福模板', '祝福', '祝您节日快乐，阖家幸福！', 'active', '运营');

-- Sample groups
INSERT IGNORE INTO contact_group (name, owner_dept, member_count, tags, status) VALUES
('全员通知群', '综合办公室', 0, '全员,通知', 'enabled'),
('应急响应群', '安全管理部', 0, '应急,核心', 'enabled'),
('技术团队群', '技术研发部', 0, '技术', 'enabled');
