# 短信系统开发计划

## 技术栈
- Frontend: React + Ant Design + Vite
- Backend: Spring Boot + MyBatis + MySQL
- Docs: Markdown + Mermaid

## 阶段计划
1. 阶段1：需求与设计 ✅
2. 阶段2：基础工程与仓库初始化 ✅
3. 阶段3：通讯录 / 群组 / 模板 ✅（接口首版）
4. 阶段4：简讯录入与预览 🚧
5. 阶段5：发送任务与预约发送
6. 阶段6：查询、详情、复制发送
7. 阶段7：HR同步 / 网关联调 / 审计统计

## 当前里程碑产出
- 架构设计文档：`docs/architecture.md`
- 前端：React + Ant Design 驾驶舱页面与创建任务表单
- 后端：Spring Boot + MyBatis 基础工程、Dashboard/Task/Contacts/Groups/Templates 接口
- 数据库：`backend/src/main/resources/db/schema.sql` 初始化脚本

## 下一阶段目标
- 阶段4：简讯录入页（草稿保存、模板套用、预览）
- 阶段5：预约发送参数与任务状态流转
- 用 MyBatis Mapper 替换当前 MockDataService 内存数据
