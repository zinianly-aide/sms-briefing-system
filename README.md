# SMS Briefing System

企业内部短信简讯管理平台，目标覆盖：通讯录、群组、模板、简讯录入、发送任务、查询追踪、统计审计。

## 技术栈
- Frontend: React + Vite + Ant Design
- Backend: Spring Boot 3 + MyBatis + MySQL

## 当前进度
- ✅ 阶段1：补齐系统架构设计文档
- ✅ 阶段2：搭建可运行前后端基础工程
- 🚧 阶段3：通讯录 / 群组 / 模板模块开发中

## 本地启动

### Backend
```bash
cd backend
mvn spring-boot:run
```

默认端口：`http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm run dev
```

默认端口：`http://localhost:5173`

## 已实现能力（当前里程碑）
- 仪表盘聚合接口 `/api/dashboard`
- 发送任务列表与创建接口 `/api/tasks`
- Ant Design 运营看板页面
- 快速创建简讯任务表单
- 通讯录群组 / 模板 / 任务示例数据展示

## 文档
- `docs/project-plan.md`：阶段计划
- `docs/architecture.md`：架构设计与 API 首版规划
