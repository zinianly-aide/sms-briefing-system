# SMS Briefing System 架构设计

## 1. 目标
构建企业内部“短信简讯管理平台”，覆盖通讯录维护、群组编排、模板中心、简讯录入、发送任务、查询追踪、统计审计。

## 2. 技术选型
- Frontend：React + Vite + Ant Design
- Backend：Spring Boot 3 + MyBatis + MySQL 8
- 集成预留：HR 通讯录同步、短信网关、审计日志、统计报表

## 3. 角色与边界
- 管理员：维护模板、群组、系统配置
- 运营人员：创建简讯、编排发送任务、查看结果
- 审批/审计角色：审核关键模板与高优先级发送记录

## 4. 模块拆分
1. 主数据中心：联系人、群组、模板
2. 简讯中心：草稿、预览、变量渲染、敏感词校验
3. 任务中心：立即发送、预约发送、重发、复制发送
4. 查询与统计：任务详情、发送回执、失败原因、渠道统计
5. 集成中心：HR、短信网关、审计日志

## 5. 数据模型（首版）
- contact：联系人主档，手机号、部门、在岗状态、标签
- contact_group：群组定义，支持手工筛选/规则筛选
- briefing_template：模板内容与状态
- briefing_record：简讯草稿/已提交内容
- send_task：发送任务主表
- send_task_detail：每个接收人发送结果

## 6. API 首批规划
- `GET /api/dashboard`：运营看板聚合数据
- `GET /api/tasks`：任务列表
- `POST /api/tasks`：创建发送任务
- 后续扩展：contacts/groups/templates CRUD、task detail、stats

## 7. 开发阶段映射
- 已完成：阶段1文档补齐、阶段2可运行骨架
- 下一步：阶段3 通讯录 / 群组 / 模板 CRUD + MyBatis 持久化
